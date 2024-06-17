insert into board_users(NAME, PROFILE_IMAGE, ID) values('테스트', 'blank-profile-picture.png', 'a2240b59-47f6-4ad4-ba07-f7c495909f40');
insert into board_users(NAME, PROFILE_IMAGE, ID) values('테스트2', 'blank-profile-picture.png', '3465335b-5457-4219-a0b2-d0c8b79d16ac');
insert into board_users(NAME, PROFILE_IMAGE, ID) values('테스트3', 'blank-profile-picture.png', '4ba22d11-66a2-483a-a255-12bcb91d8e49');

insert into boards(WRITER_ID, TITLE, CONTENTS, COUNT, COUNT_OF_ANSWER, CREATE_DATE, UPDATE_DATE, IS_DELETED) values('a2240b59-47f6-4ad4-ba07-f7c495909f40', 'test', 'test', 0, 0, '2018-07-16 15:55:20.879', '2018-07-16 15:55:20.879', false);
insert into boards(WRITER_ID, TITLE, CONTENTS, COUNT, COUNT_OF_ANSWER, CREATE_DATE, UPDATE_DATE, IS_DELETED) values('a2240b59-47f6-4ad4-ba07-f7c495909f40', 'test2', 'test2', 0, 0, '2018-07-17 15:55:20.879', '2018-07-17 15:55:20.879', false);
insert into boards(WRITER_ID, TITLE, CONTENTS, COUNT, COUNT_OF_ANSWER, CREATE_DATE, UPDATE_DATE, IS_DELETED) values('a2240b59-47f6-4ad4-ba07-f7c495909f40', 'test3', 'test3', 0, 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);
insert into boards(WRITER_ID, TITLE, CONTENTS, COUNT, COUNT_OF_ANSWER, CREATE_DATE, UPDATE_DATE, IS_DELETED) values('3465335b-5457-4219-a0b2-d0c8b79d16ac', 'testtest', 'testtest', 0, 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), false);