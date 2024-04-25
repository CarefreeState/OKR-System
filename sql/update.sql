
update personal_okr set is_deleted = 1 where id in (135, 134, 133, 132, 130, 129);

update personal_okr set is_deleted = 0 where id in (82, 66);



update okr_core set is_deleted = 1 where id = 642;

update okr_core set is_deleted = 1 where id = 641;

update okr_core set is_over = 1 where id = 183;

update okr_core set summary = "无" where id = 183;

update first_quadrant set objective = '练就降龙十八掌' where id = 211;

update first_quadrant set objective = '练就控鹤擒龙' where id = 182;

update first_quadrant set deadline = '2024-06-30 23:59:59' where id = 183;

update team_okr set is_deleted = 1 where id in (81, 79, 77, 75, 74, 72);

update team_okr set is_deleted = 1 where id in (85);

update team_okr set parent_team_id = 16 where id in (42, 44);

update team_okr set is_deleted = 1 where id in (18, 80);

update team_okr set team_name = '快乐星球' where id in (43);



update team_personal_okr set is_deleted = 1 where id in (168, 163);

update day_record set is_deleted = 1 where id in (7, 8);

insert into day_record (id, core_id, record_date, credit1, credit2, credit3, credit4) values
    (9 , 183, '2024-04-18', 80, 4, 0, 60),
    (10, 183, '2024-04-19', 75, 7, 1, 70),
    (11, 183, '2024-04-20', 80, 5, 1, 60),
    (12, 183, '2024-04-21', 85, 5, 0, 70),
    (13, 183, '2024-04-22', 85, 3, 1, 70),
    (14, 183, '2024-04-23', 80, 4, 2, 60),
    (15, 183, '2024-04-24', 90, 7, 1, 70)
;

update core_recorder set record_map = "{\"dayRecordId\": 15}" where id = 5;

update user_medal set is_read = 0 where user_id = 1;
