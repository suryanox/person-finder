db-up:
	docker compose up -d

db-down:
	docker compose down

db-reset:
	docker compose down -v && docker compose up -d

start: db-up run

run:
	./gradlew bootRun

build:
	./gradlew build

test:
	./gradlew test
