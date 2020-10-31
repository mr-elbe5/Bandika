

--local ok
CREATE TABLE IF NOT EXISTS t_content_log
(
    content_id INTEGER     NOT NULL,
    day        DATE        NOT NULL,
    count      INTEGER 	   NOT NULL,
    CONSTRAINT t_content_log_pk PRIMARY KEY (content_id, day),
    CONSTRAINT t_content_log_fk1 FOREIGN KEY (content_id) REFERENCES t_content (id) ON DELETE CASCADE
);

update t_file set file_name = replace(file_name, '.jpeg', '.jpg');
update t_file set file_name = replace(file_name, '.JPG', '.jpg');

---

CREATE TABLE IF NOT EXISTS t_tmp
(
    id            INTEGER       NOT NULL,
    name          VARCHAR(255)  NOT NULL,
    file_id		  INTEGER		NOT NULL DEFAULT 0,
    ext      	  VARCHAR(100)  NOT NULL DEFAULT '',
    content      TEXT 		    NOT NULL DEFAULT ''
);

delete from t_tmp;

insert into t_tmp as t1 (id,name,file_id,content)
    (select t2.part_id,
            t2.name,
            substring(t2.content from position('/image/show/' in t2.content) + 12 for 4)::int,
            t2.content
     from t_part_field t2 where t2.content like '%/image/show/%');

update t_tmp as t1 set ext = substring(t2.file_name from position('.' in t2.file_name)  for 4) from t_file t2 where t2.id = t1.file_id;

update t_tmp set content = replace(content, concat('/ctrl/image/show/',file_id::varchar(4)), concat('/files/', file_id::varchar(4), ext));

update t_part_field as t1 set content = t2.content from t_tmp t2 where t1.part_id = t2.id and t1.name = t2.name;

delete from t_tmp;

insert into t_tmp as t1 (id,name,file_id,content)
    (select t2.part_id,
            t2.name,
            substring(t2.content from position('/document/download/' in t2.content) + 19 for 4)::int,
            t2.content
     from t_part_field t2 where t2.content like '%/document/download/%');

update t_tmp as t1 set ext = substring(t2.file_name from position('.' in t2.file_name)  for 4) from t_file t2 where t2.id = t1.file_id;

update t_tmp set content = replace(content, concat('/ctrl/document/download/',file_id::varchar(4)), concat('/files/', file_id::varchar(4), ext, '?download=true'));

update t_part_field as t1 set content = t2.content from t_tmp t2 where t1.part_id = t2.id and t1.name = t2.name;