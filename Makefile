dev-up:
	docker-compose -f docker/dev/docker-compose.dev.yml up --build -d

dev-down:
	docker-compose -f docker/dev/docker-compose.dev.yml down

prod-pull:
	docker-compose -f docker/prod/docker-compose.prod.yml pull

prod-up:
	docker-compose -f docker/prod/docker-compose.prod.yml up -d

prod-down:
	docker-compose -f docker/prod/docker-compose.prod.yml down

prod-build:
	docker-compose -f docker/prod/docker-compose.prod.yml build

logs:
	docker-compose -f docker/dev/docker-compose.dev.yml logs -f app