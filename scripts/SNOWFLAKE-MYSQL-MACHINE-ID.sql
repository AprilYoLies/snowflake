CREATE DATABASE snowflake;

drop table if exists MYSQL_MACHINE_ID_PROVIDER;

create table MYSQL_MACHINE_ID_PROVIDER
(
    ID bigint not null,
    IP varchar(64) default null,
    primary key (ID),
    unique key UK_IP (IP)
);

drop procedure if exists initMysqlMachineIdProvider;

DELIMITER //
create procedure initMysqlMachineIdProvider()
begin
    declare count int;
    set count = 0;
    set autocommit = false;

    LOOP_LABLE:
        loop
            insert into MYSQL_MACHINE_ID_PROVIDER(ID) values (count);
            set count = count + 1;
            if count >= 1024 then
                leave LOOP_LABLE;
            end if;
        end loop;

    commit;
    set autocommit = true;
end;
//
DELIMITER ;

call initMysqlMachineIdProvider;