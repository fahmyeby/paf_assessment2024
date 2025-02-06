-- Write your Task 1 answers in this file
-- Create database called bedandbreakfast.

DROP SCHEMA IF EXISTS bedandbreakfast;
CREATE SCHEMA IF NOT EXISTS bedandbreakfast;
USE bedandbreakfast;

-- drop table if exists
drop table if exists reviews;
drop table if exists bookings;
drop table if exists users;

-- create users table
create table if not exists users (
    email varchar(128) not null primary key,
    name varchar(128) not null
);

-- create bookings table
create table if not exists bookings (
    booking_id char(8) not null primary key,
    listing_id varchar(20) not null,
    email varchar(128) not null,
    duration int not null,
    foreign key (email) references users(email)
);

-- create reviews table
create table if not exists reviews (
   id int auto_increment primary key,
    date timestamp not null default current_timestamp,
    listing_id varchar(20) not null,
    reviewer_name varchar(64) not null,
    comments text not null
);

 GRANT ALL PRIVILEGES ON bedandbreakfast.* TO 'root'@'%';
 FLUSH PRIVILEGES;

--Batch insert user.csv -> users table 
INSERT INTO users(email,name) VALUES
 ('fred@gmail.com','Fred Flintstone')
,('barney@gmail.com','Barney Rubble')
,('fry@planetexpress.com','Philip J Fry')
,('hlmer@gmail.com','Homer Simpson');


