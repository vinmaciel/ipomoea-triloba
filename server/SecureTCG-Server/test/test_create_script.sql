create database test_db;

create table test_db.test_one(
	id integer primary key auto_increment,
	name varchar(255),
	data blob
);
create table test_db.test_two(
	id integer primary key auto_increment,
	description varchar(255),
	test_one_id integer,
	foreign key (test_one_id) references test_one(id)
);