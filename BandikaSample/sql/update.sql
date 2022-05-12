alter table t_user drop column token;
alter table t_user drop column token_expiration;
alter table t_user add refresh_token VARCHAR(2000) NOT NULL DEFAULT '';
alter table t_user add token_host VARCHAR(500) NOT NULL DEFAULT '';

--
CREATE TABLE IF NOT EXISTS t_template_part
(
    id            INTEGER      NOT NULL,
    template      VARCHAR(255) NOT NULL,
    CONSTRAINT t_template_part_pk PRIMARY KEY (id),
    CONSTRAINT t_template_part_fk1 FOREIGN KEY (id) REFERENCES t_page_part (id) ON DELETE CASCADE
);

insert into t_template_part (id, template) select id,layout from t_layout_part;

drop table t_layout_part;

update t_page_part set type = 'TemplatePartData' where type = 'LayoutPartData';