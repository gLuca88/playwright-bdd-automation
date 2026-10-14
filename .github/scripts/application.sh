#!/usr/bin/env bash
set -euo pipefail

repository_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
application_dir="$repository_root/target/application"
export SPRINT=sprint5
export DISABLE_LOGGING=true

compose() {
  docker compose --project-name framework-ci \
    --project-directory "$application_dir" \
    -f "$application_dir/docker-compose.yml" "$@"
}

wait_for_database() {
  for attempt in {1..60}; do
    if compose exec -T mariadb mysqladmin ping -h localhost -u root -proot --silent; then
      return 0
    fi
    echo "Waiting for database ($attempt/60)"
    sleep 2
  done
  echo "Database did not become ready" >&2
  return 1
}

wait_for_url() {
  local url="$1"
  for attempt in {1..60}; do
    if curl --fail --silent --show-error --max-time 5 "$url" -o /dev/null; then
      return 0
    fi
    echo "Waiting for $url ($attempt/60)"
    sleep 5
  done
  echo "Application did not become ready: $url" >&2
  return 1
}

case "${1:-}" in
  start)
    # Select services explicitly: Composer is run synchronously below.
    compose up -d --build mariadb laravel-api web angular-ui
    wait_for_database
    compose exec -T -u root laravel-api composer install \
      --no-dev --no-interaction --optimize-autoloader --ignore-platform-req=ext-ffi
    compose exec -T -u root laravel-api sh -c \
      'mkdir -p storage/framework/cache/data storage/framework/sessions storage/framework/views bootstrap/cache && chown -R www-data:www-data storage bootstrap/cache'
    compose exec -T laravel-api php artisan config:clear
    # This database belongs only to this disposable CI application.
    compose exec -T laravel-api php artisan migrate:fresh --seed --force
    wait_for_url http://localhost:8091/status
    wait_for_url http://localhost:4200
    ;;
  logs)
    mkdir -p "$repository_root/target/docker-logs"
    compose ps --all > "$repository_root/target/docker-logs/services.txt" 2>&1
    compose logs --no-color --timestamps > "$repository_root/target/docker-logs/containers.log" 2>&1
    ;;
  stop)
    compose down --volumes --remove-orphans
    ;;
  *)
    echo "Usage: bash .github/scripts/application.sh {start|logs|stop}" >&2
    exit 2
    ;;
esac
