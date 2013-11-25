drop database securetcg;
create database securetcg;

use securetcg;

create table class(
	id integer primary key,
	name varchar(63),
	description varchar(255),
	bitmap_path varchar(255)
);

insert into class (id, name, description, bitmap_path)
values (1,'The Windy','The Windy''s primary magic is to create strong winds. These winds can be used to blow away objects or bind creatures.','SakuraDeck\\The_windy.jpg');
insert into class (id, name, description, bitmap_path)
values (2,'The Fly','The Fly''s main ability is to grant the user flight.','SakuraDeck\\The_big.jpg');
insert into class (id, name, description, bitmap_path)
values (3,'The Shadow','Shadow can touch us, but we can''t touch it.','SakuraDeck\\The_shadow.jpg');
insert into class (id, name, description, bitmap_path)
values (4,'The Watery','The Watery is able to control water and create whirlpools and tidal waves. It can also absorb the power from other water-based cards like The Rain.','SakuraDeck\\The_watery.jpg');
insert into class (id, name, description, bitmap_path)
values (5,'The Rain','The Rain is a mischievous entity that is able to control rain and create suprising downpours on anybody to prank them.','SakuraDeck\\The_rain.jpg');
insert into class (id, name, description, bitmap_path)
values (6,'The Wood','The Wood is a gentle spirit that is able to create trees, branches, and vines.','SakuraDeck\\The_wood.jpg');
insert into class (id, name, description, bitmap_path)
values (7,'The Jump','The Jump is a card which possesses prestigious jumping abilities and can bestow them to its user.','SakuraDeck\\The_jump.jpg');
insert into class (id, name, description, bitmap_path)
values (8,'The Illusion','The Illusion is capable of creating an illusion based on whatever the viewer expects to see or wants to see.','SakuraDeck\\The_illusion.jpg');
insert into class (id, name, description, bitmap_path)
values (9,'The Silent','The Silent is a mysterious card that shuns loud noises. It is able to stifle all forms of sound, rendering even the noisiest room completely silent.','SakuraDeck\\The_silent.jpg');
insert into class (id, name, description, bitmap_path)
values (10,'The Thunder','The Thunder is able to summon bolts of lightning.','SakuraDeck\\The_thunder.jpg');
insert into class (id, name, description, bitmap_path)
values (11,'The Sword','The Sword is able to cut through almost anything, including magical barriers.','SakuraDeck\\The_sword.jpg');
insert into class (id, name, description, bitmap_path)
values (12,'The Flower','The Flower has a gentle and fun loving spirit, able to create any kind of flower or blossom.','SakuraDeck\\The_flower.jpg');
insert into class (id, name, description, bitmap_path)
values (13,'The Shield','The Shield is able to create a shell, force field or barrier around its target that is impenetrable to almost all physical and magical attacks.','SakuraDeck\\The_shield.jpg');
insert into class (id, name, description, bitmap_path)
values (14,'The Time','The Time has the ability to freely control the passage of time.','SakuraDeck\\The_time.jpg');
insert into class (id, name, description, bitmap_path)
values (15,'The Power','The Power is able to give its user an incredible amount of physical strength.','SakuraDeck\\The_power.jpg');
insert into class (id, name, description, bitmap_path)
values (16,'The Mist','The Mist is a corrosive entity, able to eat through almost any material including metal bars and stone.','SakuraDeck\\The_mist.jpg');
insert into class (id, name, description, bitmap_path)
values (17,'The Storm','The Storm is capable of summoning huge rainstorms and tornadoes that can be used both to attack an opponent and to bind them.','SakuraDeck\\The_storm.jpg');
insert into class (id, name, description, bitmap_path)
values (18,'The Float','The Float, as its name implies, likes to make things float.','SakuraDeck\\The_float.jpg');
insert into class (id, name, description, bitmap_path)
values (19,'The Erase','The Erase has the power to make almost anything, including people, disappear.','SakuraDeck\\The_erase.jpg');

create table player(
	id integer primary key auto_increment,
	name varchar(127) not null,
	pku blob not null
);
