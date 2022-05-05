# catalog-rest-service
This project exposes the rest services for the app catalog project.
The rest service api is developed in reactive Java with Spring WebFlux.

## Run locally

```
mvn spring-boot:run  -Dspring-boot.run.arguments="--POSTGRES_USERNAME=dummy \
                      --POSTGRES_PASSWORD=dummy \
                      --POSTGRES_DBNAME=catalog \
                      --POSTGRES_SERVICE=localhost:5432
                      --TRUST_ORIGIN=http://localhost:8082
                      --DB_SSLMODE=DISABLE --server.port=8083"

```
`DB_SSLMODE=REQUIRE` if connecting with ssl support.  For local development use `DISABLE`
`TRUST_ORIGIN` set to localhost:8082 from where the Vue webclient is hosted on.

 
 
## Build Docker image

Build docker image using included Dockerfile.


`docker build -t ghcr.io/catalog-rest-service:latest .` 

## Push Docker image to repository

`docker push ghcr.io/catalog-rest-service:latest`

## Deploy Docker image locally

`docker run -e POSTGRES_USERNAME=dummy \
 -e POSTGRES_PASSWORD=dummy -e POSTGRES_DBNAME=catlog \
  -e POSTGRES_SERVICE=localhost:5432 \
 --publish 8080:8080 imageregistry/project-rest-service:1.0`



##Instruction for port-forwarding database pod
```
export PGMASTER=$(kubectl get pods -o jsonpath={.items..metadata.name} -l application=spilo,cluster-name=project-minimal-cluster,spilo-role=master -n yournamesapce); 
echo $PGMASTER;
kubectl port-forward $PGMASTER 6432:5432 -n backend;
```

###Login to database instruction
```
export PGPASSWORD=$(kubectl get secret <SECRET_NAME> -o 'jsonpath={.data.password}' -n backend | base64 -d);
echo $PGPASSWORD;
export PGSSLMODE=require;
psql -U <USER> -d projectdb -h localhost -p 6432

```