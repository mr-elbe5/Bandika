
--
CREATE TABLE IF NOT EXISTS t_template_page
(
    id            INTEGER      NOT NULL,
    template_type VARCHAR(20)  NOT NULL DEFAULT 'PAGE',
    template      VARCHAR(255) NULL,
    CONSTRAINT t_template_page_pk PRIMARY KEY (id),
    CONSTRAINT t_template_page_fk1 FOREIGN KEY (id) REFERENCES t_page (id) ON DELETE CASCADE,
    CONSTRAINT t_template_page_fk2 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
--
CREATE SEQUENCE s_page_part_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_page_part
(
    id            INTEGER      NOT NULL,
    type          VARCHAR(30)  NOT NULL DEFAULT 'PagePartData',
    name          VARCHAR(60)  NOT NULL DEFAULT '',
    creation_date TIMESTAMP    NOT NULL DEFAULT now(),
    change_date   TIMESTAMP    NOT NULL DEFAULT now(),
    flex_class    VARCHAR(255) NOT NULL DEFAULT '',
    CONSTRAINT t_page_part_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS t_template_page_part
(
    id            INTEGER      NOT NULL,
    template_type VARCHAR(20)  NOT NULL DEFAULT 'PART',
    template      VARCHAR(255) NOT NULL,
    css_classes   VARCHAR(255) NOT NULL DEFAULT '',
    script        TEXT         NOT NULL DEFAULT '',
    CONSTRAINT t_template_page_part_pk PRIMARY KEY (id),
    CONSTRAINT t_template_page_part_fk1 FOREIGN KEY (id) REFERENCES t_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_template_page_part_fk2 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
CREATE TABLE IF NOT EXISTS t_part_field
(
    part_id    INTEGER     NOT NULL,
    field_type VARCHAR(60) NOT NULL,
    name       VARCHAR(60) NOT NULL DEFAULT '',
    content    TEXT        NOT NULL DEFAULT '',
    CONSTRAINT t_part_field_pk PRIMARY KEY (part_id, name),
    CONSTRAINT t_part_field_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id)
);
--
CREATE TABLE IF NOT EXISTS t_page_part2page
(
    part_id  INTEGER     NOT NULL,
    page_id  INTEGER     NULL,
    section  VARCHAR(60) NOT NULL,
    position INTEGER     NOT NULL DEFAULT 0,
    CONSTRAINT t_page_part2page_pk PRIMARY KEY (part_id, page_id, section, position),
    CONSTRAINT t_page_part2page_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_page_part2page_fk2 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);

insert into t_template (change_date,display_name,description,code,name,type) values(now(),'HomePage','','','homePage','PAGE');
