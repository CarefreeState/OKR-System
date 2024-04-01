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


select
    f.*, k.id k_id, k.first_quadrant_id k_firstQuadrantId, k.content k_content,
    k.probability k_probability, k.version k_version, k.is_deleted k_isDeleted,
    k.create_time k_createTime, k.update_time k_updateTime
from
    first_quadrant f left join key_result k on f.id = k.first_quadrant_id and k.is_deleted = 0
where
    f.core_id = 1 and f.is_deleted = 0
order by k.probability, k.create_time
;
select
    s.*,
    p1.id p1_id, p1.second_quadrant_id p1_second_quadrant_id, p1.content p1_content, p1.is_completed p1_is_completed,
    p2.id p2_id, p2.second_quadrant_id p2_second_quadrant_id, p2.content p2_content, p2.is_completed p2_is_completed
from
    second_quadrant s left join priority_number_one p1 on s.id = p1.second_quadrant_id and  p1.is_deleted = 0
                      left join priority_number_two p2 on s.id = p2.second_quadrant_id and p2.is_deleted = 0
where
    s.core_id = 2 and s.is_deleted = 0
order by p1.is_completed, p2.is_completed, p1.create_time, p2.create_time
;

select
    t.*,
    a.id a_id, a.third_quadrant_id a_third_quadrant_id, a.content a_content, a.is_completed a_is_completed
from
    third_quadrant t left join action a on t.id = a.third_quadrant_id and a.is_deleted = 0
where
    t.core_id = 2 and t.is_deleted = 0
order by a.is_completed, a.create_time
;

select
    fq.*,
    sf.id sf_id, sf.fourth_quadrant_id sf_fourth_quadrant_id, sf.label sf_label, sf.color sf_color
from
    fourth_quadrant fq left join status_flag sf on fq.id = sf.fourth_quadrant_id and sf.is_deleted = 0
where
    fq.core_id = 2 and fq.is_deleted = 0
order by sf.create_time
;

select
    o.id,
    f.id f_id, f.core_id f_core_id, f.objective f_objective,
    k.id k_id, k.first_quadrant_id k_first_quadrant_id, k.content k_content, k.probability k_probability,
    s.id s_id, s.core_id s_core_id,
    p1.id p1_id, p1.second_quadrant_id p1_second_quadrant_id, p1.content p1_content, p1.is_completed p1_is_completed,
    p2.id p2_id, p2.second_quadrant_id p2_second_quadrant_id, p2.content p2_content, p2.is_completed p2_is_completed,
    t.id t_id, t.core_id t_core_id,
    a.id a_id, a.third_quadrant_id a_third_quadrant_id, a.content a_content, a.is_completed a_is_completed,
    fq.id fq_id, fq.core_id fq_core_id,
    sf.id sf_id, sf.fourth_quadrant_id sf_fourth_quadrant_id, sf.label sf_label
from
    okr_core o,
    first_quadrant f left join key_result k on f.id = k.first_quadrant_id,
    second_quadrant s left join priority_number_one p1 on s.id = p1.second_quadrant_id
                      left join priority_number_two p2 on s.id = p2.second_quadrant_id,
    third_quadrant t left join action a on t.id = a.third_quadrant_id,
    fourth_quadrant fq left join status_flag sf on fq.id = sf.fourth_quadrant_id
where
    o.id = 2
  and f.core_id = o.id
  and s.core_id = o.id
  and t.core_id = o.id
  and fq.core_id = o.id
;

UPDATE status_flag SET is_deleted=1 WHERE is_deleted=0 AND (id = 1);

select * from status_flag;

select
    o.id,
    f.deadline f_deadline,
    s.id s_id, s.deadline s_deadline, o.second_quadrant_cycle,
    t.id t_id, t.deadline t_deadline, o.third_quadrant_cycle
from
    okr_core o, first_quadrant f, second_quadrant s, third_quadrant t
where
    o.is_over = 0 and o.id = f.core_id and o.id = s.core_id and o.id = t.core_id;

SET global max_sp_recursion_depth = 255;

DELIMITER //

DROP PROCEDURE IF EXISTS GetTreeNodes;
DROP PROCEDURE IF EXISTS GetSubtreeNodes;

drop temporary table if exists temp_tree_nodes;
CREATE TEMPORARY TABLE IF NOT EXISTS temp_tree_nodes (
    id bigint,
    parent_team_id bigint,
    team_name VARCHAR(32)
);

CREATE PROCEDURE GetTreeNodes(IN root_id bigint)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur_id, parent_id bigint;
    DECLARE cur_name VARCHAR(32);
    DECLARE nodes_cursor CURSOR FOR
SELECT id, parent_team_id, team_name
FROM team_okr
WHERE (id = root_id OR parent_team_id = root_id) and is_deleted = 0;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    CREATE TEMPORARY TABLE IF NOT EXISTS temp_tree_nodes (
        id bigint,
        parent_team_id bigint,
        team_name VARCHAR(32)
    );

OPEN nodes_cursor;
read_loop: LOOP
        FETCH nodes_cursor INTO cur_id, parent_id, cur_name;
        IF done THEN
            LEAVE read_loop;
END IF;

INSERT INTO temp_tree_nodes (id, parent_team_id, team_name)
VALUES (cur_id, parent_id, cur_name);

CALL GetSubtreeNodes(cur_id);
END LOOP;

CLOSE nodes_cursor;

SELECT distinct * FROM temp_tree_nodes order by id;

DROP TEMPORARY TABLE IF EXISTS temp_tree_nodes;
END//

CREATE PROCEDURE GetSubtreeNodes(IN parent_id bigint)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur_id, cur_parent_id bigint;
    DECLARE cur_name VARCHAR(32);
    DECLARE subtree_cursor CURSOR FOR
SELECT id, parent_team_id, team_name
FROM team_okr
WHERE parent_team_id = parent_id and is_deleted = 0;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

OPEN subtree_cursor;
read_loop: LOOP
        FETCH subtree_cursor INTO cur_id, cur_parent_id, cur_name;
        IF done THEN
            LEAVE read_loop;
END IF;

INSERT INTO temp_tree_nodes (id, parent_team_id, team_name)
VALUES (cur_id, cur_parent_id, cur_name);

CALL GetSubtreeNodes(cur_id);
END LOOP;

CLOSE subtree_cursor;
END//

DELIMITER ;

DELIMITER //

drop procedure if exists find_root_node;

CREATE PROCEDURE find_root_node (IN start_id BIGINT)
BEGIN
    DECLARE current_id BIGINT;
    DECLARE parent_id BIGINT;

    SET current_id = start_id;
    SET parent_id = NULL;

    node_loop: LOOP
SELECT parent_team_id INTO parent_id
FROM team_okr
WHERE id = current_id and is_deleted = 0;

IF parent_id IS NOT NULL THEN
            SET current_id = parent_id;
ELSE
SELECT id, parent_team_id, team_name
FROM team_okr
WHERE id = current_id and is_deleted = 0;
LEAVE node_loop;
END IF;
END LOOP;

END //

DELIMITER ;


CALL find_root_node(5);

call GetTreeNodes(3);


select t.id, t.core_id, t.team_name, t.parent_team_id, t.manager_id, t.create_time, o.is_over, k.id k_id, k.first_quadrant_id k_first_quadrant_id, k.content k_content, k.probability k_probability
from team_okr t, okr_core o,first_quadrant f
                                left join key_result k on f.id = k.first_quadrant_id
where t.is_deleted = 0 and f.is_deleted = 0 and o.is_deleted = 0
  and (k.is_deleted = 0 or k.is_deleted is null) and t.id in ( 1 )
  and t.core_id = o.id and f.core_id = o.id order by t.id;


select
    tp.user_id, tp.create_time, u.nickname, u.email, u.phone, u.photo
from
    team_okr t, team_personal_okr tp, user u
where
    t.id = 3
  and t.is_deleted = 0 and tp.is_deleted = 0 and u.is_deleted = 0
  and t.id = tp.team_id
  and tp.user_id = u.id
  and u.id != t.manager_id
order by tp.create_time desc