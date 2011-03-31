create table userbase (
	id bigint not null auto_increment, 
	email varchar(255) unique, 
	passwordhash varchar(64), 
	regdate timestamp, 
	primary key (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8