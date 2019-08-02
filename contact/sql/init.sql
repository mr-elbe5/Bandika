
--
CREATE TABLE IF NOT EXISTS t_contact_page_part
(
    id           INTEGER       NOT NULL,

    CONSTRAINT t_content_page_part_pk PRIMARY KEY (id),
    CONSTRAINT t_content_page_part_fk1 FOREIGN KEY (id) REFERENCES t_page_part (id) ON DELETE CASCADE
);


