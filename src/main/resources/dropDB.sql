alter table post_comments drop foreign key comment2post_fk;
alter table post_comments drop foreign key comment2user_fk;
alter table posts drop foreign key post2moder_fk;
alter table posts drop foreign key post2user_fk;
alter table post_votes drop foreign key vote2post_fk;
alter table post_votes drop foreign key vote2user_fk;
alter table tag2post drop foreign key tag2post_fk;
alter table tag2post drop foreign key tag2user_fk;
alter table users_roles drop foreign key FKj6m8fwv7oqv74fcehir1a9ffy;
alter table users_roles drop foreign key FK2o0jvgh89lemvvo17cbqvdxaa;
drop table if exists captcha_codes;
drop table if exists global_settings;
drop table if exists hibernate_sequence;
drop table if exists post_comments;
drop table if exists posts;
drop table if exists post_votes;
drop table if exists tag2post;
drop table if exists tags;
drop table if exists users;
drop table if exists user;
drop table if exists roles;
drop table if exists users_roles;
drop table if exists flyway_schema_history;

/*********************** ОЧИСТКА ТАБЛИЦ ******************/
/*delete from user where name='First_User';
delete from post_comments where id=1;
delete from tag2post where id=1;
delete from tags where id=1;
delete from posts where id=1;*/

/*********************** УДАЛЕНИЕ МИГРАЦИИ № 3  *****************/
# delete  from flyway_schema_history where version=1;
# delete  from flyway_schema_history where version=2;
# delete  from flyway_schema_history where version=3;