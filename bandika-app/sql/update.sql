-- salt is V3xfgDrxdl8=
-- root user
update t_user set pwd='A0y3+ZmqpMhWA21VFQMkyY6v74Y=' where id=1;

-- root page
SELECT ADDSECTIONPAGE(1, null, 'home', 'Home', 'Startseite', 1, 'defaultSectionPage');
UPDATE t_page set publish_date=now() where id =1;


--local ok

CREATE TABLE IF NOT EXISTS t_content_log
(
    content_id INTEGER     NOT NULL,
    day        DATE        NOT NULL,
    count      INTEGER 	   NOT NULL,
    CONSTRAINT t_content_log_pk PRIMARY KEY (content_id, day),
    CONSTRAINT t_content_log_fk1 FOREIGN KEY (content_id) REFERENCES t_content (id) ON DELETE CASCADE
);
