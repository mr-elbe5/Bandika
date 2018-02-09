-- reader
INSERT INTO t_user (id, last_name, first_name, email, login, pwd, pkey, approval_code, approved)
VALUES (800, 'Reader', 'Test', 'reader@localhost', 'reader', 'NrZx2YuMChQHJyVOlmqg3jJfzFI=', 'nLGWK7H7CU0=', '', TRUE);
-- system is Global Administrator
INSERT INTO t_user2group (user_id, group_id, relation) VALUES (800, 4, 'RIGHTS');

INSERT INTO t_user (id, last_name, first_name, email, login, pwd, pkey, approval_code, approved)
VALUES (801, 'Editor', 'Test', 'editor@localhost', 'editor', 'NrZx2YuMChQHJyVOlmqg3jJfzFI=', 'nLGWK7H7CU0=', '', TRUE);
-- system is Global Administrator
INSERT INTO t_user2group (user_id, group_id, relation) VALUES (801, 3, 'RIGHTS');

INSERT INTO t_user (id, last_name, first_name, email, login, pwd, pkey, approval_code, approved)
VALUES
  (802, 'Approver', 'Test', 'approver@localhost', 'approver', 'NrZx2YuMChQHJyVOlmqg3jJfzFI=', 'nLGWK7H7CU0=', '', TRUE);
-- system is Global Administrator
INSERT INTO t_user2group (user_id, group_id, relation) VALUES (802, 2, 'RIGHTS');

INSERT INTO t_user (id, last_name, first_name, email, login, pwd, pkey, approval_code, approved)
VALUES
  (803, 'Administrator', 'Test', 'admin@localhost', 'admin', 'NrZx2YuMChQHJyVOlmqg3jJfzFI=', 'nLGWK7H7CU0=', '', TRUE);
-- system is Global Administrator
INSERT INTO t_user2group (user_id, group_id, relation) VALUES (803, 1, 'RIGHTS');

