select
    count(*)
from user u, personal_okr p, okr_core o
where
    u.id = 21 and u.is_deleted = 0 and p.is_deleted = 0 and o.is_deleted = 0
  and u.id = p.user_id
  and o.id = p.core_id
  and o.is_over = 0
;

select
    p.id, p.core_id, f.objective, o.is_over, p.create_time, p.update_time
from user u, personal_okr p, okr_core o, first_quadrant f
where
    u.id = 21
  and u.is_deleted = 0 and p.is_deleted = 0 and o.is_deleted = 0 and f.is_deleted = 0
  and u.id = p.user_id
  and o.id = p.core_id
  and o.id = f.core_id
order by o.is_over, p.create_time desc
;

select
    t.id, o.id, t.team_name, o.is_over, t.create_time, t.update_time
from user u, team_okr t, okr_core o, team_personal_okr tp
where
    u.id = 21
  and u.is_deleted = 0 and t.is_deleted = 0 and o.is_deleted = 0 and tp.is_deleted = 0
  and u.id = tp.user_id
  and t.core_id = o.id
  and tp.team_id = t.id
order by o.is_over, o.create_time desc
;

select
    tp.id, o.id, tp.team_id, t.team_name, o.is_over, tp.create_time, tp.update_time
from user u, team_okr t, okr_core o, team_personal_okr tp
where
    u.id = 21
  and u.is_deleted = 0 and t.is_deleted = 0 and o.is_deleted = 0 and tp.is_deleted = 0
  and u.id = tp.user_id
  and tp.core_id = o.id
  and tp.team_id = t.id
order by o.is_over, o.create_time desc
;

select
    t.id, t.core_id, t.team_name, t.parent_team_id, t.manager_id, t.create_time, o.is_over, o.degree,
    k.id k_id, k.first_quadrant_id k_first_quadrant_id,
    k.content k_content, k.probability k_probability
from
    team_okr t, okr_core o,
    first_quadrant f left join key_result k on f.id = k.first_quadrant_id
where
    t.is_deleted = 0 and t.is_deleted = 0 and o.is_deleted = 0 and k.is_deleted = 0
  and t.id in (1, 2, 3)
  and t.core_id = o.id
  and f.core_id = o.id
order by t.id
;


select id, parent_team_id, manager_id , team_name
from team_okr
where team_name in ('水晶帝国', '暮光城堡', '坎特洛特', '暮光城堡', '无尽之森') or team_name like '古希腊%';


select id, team_id, user_id
from team_personal_okr
where team_id = 3 and user_id = 186;

update team_okr
set parent_team_id = 3
where id in (9, 16);

update team_personal_okr
set team_id = 3
where id in (49, 71);

update team_okr
set parent_team_id = 27
where id = 16;

update team_personal_okr
set team_id = 27
where id = 71;


update key_result
set content = '接口开发面向扩展，并且完成度大于1'
where id = 4;

update key_result
set content = '接口性能达到优良以上，用户拥有较好的体验'
where id = 5;

update key_result
set content = '拥有严格的业务逻辑的校验、数据一致的保证、恶意操作的防御'
where id = 6;
