
ALTER TABLE t_configuration RENAME COLUMN key TO config_key;
ALTER TABLE t_configuration RENAME COLUMN value TO config_value;

ALTER TABLE t_file RENAME COLUMN size to file_size;
ALTER TABLE t_file_thumbnail RENAME COLUMN size TO file_size;


