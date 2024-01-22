select
    f.*, k.id k_id, k.first_quadrant_id k_firstQuadrantId, k.content k_content,
    k.probability k_probability, k.version k_version, k.is_deleted k_isDeleted,
    k.create_time k_createTime, k.update_time k_updateTime
from
    first_quadrant f left join key_result k on f.id = k.first_quadrant_id
where
    f.core_id = 1
order by k.probability, k.create_time
;
select
    s.*,
    p1.id p1_id, p1.second_quadrant_id p1_second_quadrant_id, p1.content p1_content, p1.is_completed p1_is_completed,
    p2.id p2_id, p2.second_quadrant_id p2_second_quadrant_id, p2.content p2_content, p2.is_completed p2_is_completed
from
    second_quadrant s left join priority_number_one p1 on s.id = p1.second_quadrant_id
                      left join priority_number_two p2 on s.id = p2.second_quadrant_id
where
    s.core_id = 2
order by p1.is_completed, p2.is_completed, p1.create_time, p2.create_time
;

select
    t.*,
    a.id a_id, a.third_quadrant_id a_third_quadrant_id, a.content a_content, a.is_completed a_is_completed
from
    third_quadrant t left join action a on t.id = a.third_quadrant_id
where
    t.core_id = 2
order by a.is_completed, a.create_time
;

select
    fq.*,
    sf.id sf_id, sf.fourth_quadrant_id sf_fourth_quadrant_id, sf.label sf_label
from
    fourth_quadrant fq left join status_flag sf on fq.id = sf.fourth_quadrant_id
where
    fq.core_id = 2
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