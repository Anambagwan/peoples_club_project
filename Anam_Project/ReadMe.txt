Case Study 1: 

People's Club:

This application will maintain the details of all club members who have registered for the membership. Based on membership type, club member will be able to avail various facilities in the club.

Membership type can be Regular Members, Premium members and Gold members. Validity of the membership will be based on the type of membership. A person can register for the membership and can update their details. On registration, a membership id will be generated for the person who have successfully registered for the club. Admin can manage types of memberships such as adding new membership schemes, modify existing schemes and remove any membership schemes.

A member can also view the expiry date of membership and can also check the number of days or month left.

Design the database schema for the above case study and integrate it with the application.

Create this in Oracle SQL 10g database:

1.

create sequence member_seq
start with 102
increment by 1;

2.

create sequence membership_seq
start with 2
increment by 1;

3.

create sequence admin_seq
start with 2
increment by 1;

4.

create table membership_types (
membership_id int primary key,
membership_name varchar2(50) not null,
validity_months int not null,
benefits CLOB
);

5.

create table members (
member_id int primary key,
first_name varchar2(20) not null,
last_name varchar2(20) not null,
email varchar2(50) not null unique,
phone varchar2(10),
membership_id int not null,
registration_date date not null,
expiry_date date,
password varchar2(255) not null,
foreign key (membership_id) references membership_types(membership_id) 
on delete cascade
);

6.

create table admin (
admin_id int primary key,
username varchar2(50) unique not null,
password varchar2(255) not null
);

select * from admin;
select * from members;
select * from membership_types;
