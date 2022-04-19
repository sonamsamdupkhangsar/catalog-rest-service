CREATE TABLE if not exists Account (id UUID PRIMARY KEY, user_id UUID, active boolean, access_date_time timestamp);
create table if not exists Application(id UUID PRIMARY KEY, name varchar, deprecated boolean, description varchar,
 git_repo varchar, documentation_url varchar, platform_id UUID);

create table if not exists Application_Environment (application_id UUID, environment_id UUID, primary key(application_id, environment_id));

create table if not exists Application_Service_Status(id uuid primary key, application_id UUID, service_id UUID, environment_id UUID,
http_status_value int, local_date_time timestamp, service_endpoint varchar, last_ping_date_time timestamp,
success_ping_count int, exception_message varchar);

create table if not exists Application_Status (id uuid primary key, application_id UUID, application_name varchar, dev_status varchar,
 dev_environment_id UUID, platform_id UUID, platform varchar, stage_status varchar, stage_environment_id UUID,
  prod_status varchar, prod_environment_id UUID);

create table if not exists Cluster (id UUID primary key, name varchar);
create table if not exists Component(id UUID primary key, name varchar, parent_id UUID, created timestamp);
create table if not exists Connection(id UUID primary key, connection varchar, app_id_source UUID, target_id UUID, connecting varchar);
create table if not exists Dependency(id UUID primary key, provider_id UUID, consumer_id UUID);
create table if not exists Environment(id UUID primary key, sort_order int, environment_type varchar, name varchar, domain varchar,
deployment_link varchar, cluster_id UUID);
create table if not exists Service( id UUID primary key, name varchar, application_id UUID, description varchar, endpoint varchar,
health_endpoint boolean, access_token_required boolean, ping_it boolean, rest_method varchar);