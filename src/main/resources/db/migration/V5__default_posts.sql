/*Moderator*/
INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (1, 1, 'ACCEPTED', 'Я - Модератор. И я тут главный! Кто меня ослушается, получит бан.', '2021/01/01',
        'Важно!', 0, NULL, 1);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (2, 1, 'ACCEPTED', 'Это самый старый пост.', '2000/01/01',
        'Самый старый пост', 0, NULL, 1);


INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (3, 1, 'ACCEPTED', 'Капитал - это мертвый труд, который, как вампир, живет только сосанием живого труда и живет тем больше, чем больше труда он сосет.', '2020/02/07',
        'Про вампиров', 0, NULL, 2);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (4, 1, 'ACCEPTED', 'Любимый девиз: De omnibus dubitandum [Все должно быть подвергнуто сомнению].', '2020/02/07',
        'Мой девиз', 0, NULL, 2);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (5, 1, 'ACCEPTED', 'Пусть правящие классы дрожат от коммунистической революции. Пролетариям нечего терять, кроме своих цепей. У них есть мир, чтобы победить. Рабочие всех стран объединяйтесь!', '2020/02/07',
        'Про революцию', 0, NULL, 2);


INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (6, 1, 'ACCEPTED', 'Общество не может освободить себя, не освободив каждого отдельного человека.', '2020/02/07',
        'Про свободу', 0, NULL, 3);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (7, 1, 'ACCEPTED', 'Решения, принятые сгоряча, всегда представляются нам необычайно благородными и героическими, но, как правило, приводят к глупостям.', '2020/02/15',
        'Про решения', 0, NULL, 3);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (8, 1, 'ACCEPTED', 'Человек перестал быть рабом человека и стал рабом вещи.', CURRENT_DATE,
        'Про рабство', 0, NULL, 3);


INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (9, 1, 'ACCEPTED', 'История меня оправдает. Можете осуждать меня. Это не имеет значения – история меня оправдает.', '2020/02/16',
        'Про меня', 0, NULL, 4);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (10, 1, 'ACCEPTED', 'Тот, кто не способен бороться за других, никогда не будет в состоянии бороться за самого себя.', '2020/02/17',
        'Про борьбу', 0, NULL, 4);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (11, 1, 'ACCEPTED', 'Скажи мне, в каком виде спорта ты выступаешь, и я скажу, кто тебя колонизировал.', '2020/03/15',
        'Про спорт', 0, NULL, 4);


INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (12, 1, 'ACCEPTED', 'Профсоюзы — школа коммунизма.', '2020/03/15',
        'Про школу', 0, NULL, 5);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (13, 1, 'ACCEPTED', 'Абстрактной истины нет, истина всегда конкретна.', '2020/04/04',
        'Про истину', 0, NULL, 5);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (14, 1, 'ACCEPTED', 'Богатые и жулики - это две стороны одной медали.', '2020/04/05',
        'Про богатство', 0, NULL, 5);


INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (15, 1, 'ACCEPTED', 'Враг сам по себе не исчезнет.', '2020/04/06',
        'Про врагов', 0, NULL, 6);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (16, 1, 'ACCEPTED', 'Американский империализм убивает людей чужих стран, он убивает также белых и негров собственной страны..', '2020/04/06',
        'Про США', 0, NULL, 6);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (17, 1, 'ACCEPTED', 'Империализм и все реакционеры - бумажные тигры.', '2020/04/17',
        'Про империализм', 0, NULL, 6);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (18, 1, 'NEW', 'Это активный пост со статусом NEW.', '2021/01/01',
        'NEW пост', 0, NULL, 7);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (19, 1, 'NEW', 'Это ещё один активный пост со статусом NEW.', '2021/01/01',
        'NEW пост', 0, NULL, 8);

INSERT INTO posts (id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES (20, 0, 'NEW', 'Это неактивный пост со статусом NEW.', '2021/01/01',
        'Неактивный NEW пост', 0, NULL, 8);