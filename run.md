1	. Prerequisites
Before running IssueFlow, install:

	java 21

	Maven 3.9+

	PostgreSQL 15+

	Docker (optional)

Verify Java:

	java -version

Verify Maven:

	mvn -v

2. Environment Variables

Create a file:

	.env

Add:

	SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/issueflow
	SPRING_DATASOURCE_USERNAME=issueflow
	SPRING_DATASOURCE_PASSWORD=issueflow

	jwt.secret=ChangeThisSecretKeyToSomethingLong
	jwt.expiration-seconds=86400

	file.storage-dir=./storage
	file.max-size-bytes=10485760
	file.allowed-content-types=image/png,image/jpeg,application/pdf,text/plain

	escalation.cron=0 */5 * * * *
3. Database Setup

Create the database:

	createdb issueflow

Create user:

	psql -c "CREATE USER issueflow WITH PASSWORD 'issueflow';"
	psql -c "GRANT ALL PRIVILEGES ON DATABASE issueflow TO issueflow;"

4. Run Locally (Maven)

Start the backend:

	mvn spring-boot:run

The API will be available at:

	http://localhost:8080

5	. Run with Docker
Build the image:

	docker build -t issueflow .

Run with Docker Compose:

	docker compose up

This starts:

	PostgreSQL

	IssueFlow backend

	Volume for file storage

6	. Run Tests
Run all tests:

	mvn test

Run a specific test class:

	mvn -Dtest=UserServiceTest test

7	. API Overview
Each module has its own endpoints:

	Auth

	Users

	Projects

	Tickets

	Comments

	Attachments

	Audit Logs

8	. Default Roles
IssueFlow supports:

	ADMIN

	PROJECT_MANAGER

	DEVELOPER

Role assignment happens during user creation.

9	. Cleanup
Stop Docker:

	docker compose down

Remove volumes:

	docker compose down -v

10	. Useful Commands
View logs:

	docker logs issueflow

Reset database:

	dropdb issueflow
	createdb issueflow