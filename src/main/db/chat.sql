--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: links; Type: TABLE; Schema: public; Owner: IFLED; Tablespace: 
--

CREATE TABLE links (
    user_id integer NOT NULL,
    room_id integer NOT NULL
);


ALTER TABLE public.links OWNER TO "IFLED";

--
-- Name: messages; Type: TABLE; Schema: public; Owner: IFLED; Tablespace: 
--

CREATE TABLE messages (
    message_id integer NOT NULL,
    user_id integer NOT NULL,
    room_id integer NOT NULL,
    action_id integer NOT NULL,
    text text,
    status integer NOT NULL,
    "time" character varying(64) NOT NULL
);


ALTER TABLE public.messages OWNER TO "IFLED";

--
-- Name: users; Type: TABLE; Schema: public; Owner: IFLED; Tablespace: 
--

CREATE TABLE users (
    user_id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE public.users OWNER TO "IFLED";

--
-- Data for Name: links; Type: TABLE DATA; Schema: public; Owner: IFLED
--

COPY links (user_id, room_id) FROM stdin;
1	1
2	1
3	1
5	1
8	1
9	1
10	1
11	1
12	1
5	3
14	1
15	1
16	1
\.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: IFLED
--

COPY messages (message_id, user_id, room_id, action_id, text, status, "time") FROM stdin;
27	14	1	40	lkasjdf;lk;lkajsdkf	2	05:48:51
30	14	1	41	aoksf;la	1	05:48:55
31	14	1	42		1	05:48:55
32	14	1	43	lkasldfj	1	05:49:52
33	14	1	44	kj.l'	1	05:50:25
34	14	1	45	hjbn	1	05:50:28
35	14	1	46	jhjlkh	1	05:50:31
36	14	1	47	1	1	05:52:34
39	14	1	50	4	1	05:52:40
37	14	1	48	22	2	05:53:00
41	14	1	53	1ajs	2	06:06:18
40	14	1	54	2222hjj	2	06:06:44
38	14	1	55		3	06:06:55
42	14	1	56	kljaj	1	06:10:48
43	14	1	57	;ljkaskldf	1	06:11:37
45	14	1	60		3	06:11:59
44	15	1	62		3	06:12:12
46	14	1	63	hjkl	1	06:20:44
47	14	1	64	l;kajsdf	1	06:24:40
48	14	1	65	123	1	07:11:04
49	14	1	66	asdf	1	07:13:25
50	14	1	67	wertg	1	07:15:04
51	14	1	68	uiynnu	1	07:17:23
1	14	1	1		1	12:04:20
2	14	1	2	123	1	12:04:28
3	14	1	3	asfd	1	12:04:33
4	14	1	4	123	1	12:04:38
5	14	1	5	234	1	01:00:57
6	14	1	6	lkajsdf	1	02:40:35
7	14	1	7	asl;fdjk;alksdjfkl;zjsdf	1	02:40:38
8	14	1	8	klas;dlfj;aklsdjf;lkajsdfklas;kldjfasd;klfj;aklsdjf	1	02:40:50
9	14	1	9	lkajsdf	1	02:44:20
10	14	1	10	pioquweroiuqweor	1	02:44:24
11	14	1	11	poiajfpoiajsdpoifjasdf	1	02:44:27
12	14	1	12	hunvy8wbvfn8sd6vbtsdf6v	1	02:44:32
13	14	1	13	ybuhnjim	1	02:46:39
14	14	1	14	;lakjsdf;lkjasdf	1	04:03:34
15	14	1	15	;lkajsdf	1	05:11:19
16	14	1	16	uwiejsdk	1	05:11:22
17	14	1	17	iasjdfpiajsdfpiaspodfjasdf	1	05:11:24
18	14	1	18	hjik	1	05:14:22
52	14	1	69	dasfasdf	1	07:19:30
53	14	1	70	lksd;lkaja;dsklfj	1	07:19:37
54	14	1	71	ioufw	1	07:19:48
55	14	1	72	kja;skfj;alksdfj;klajsdf;lkjasd;lfkjalsdjf;laksdjf;lkajsdf	1	07:19:55
56	14	1	73	fiosdj	1	07:19:59
21	14	1	23	Editing....klkfa;sd;fkjhlk;j;kl	2	05:39:25
20	14	1	24	dsfhmjgfds\\\\\\]	2	05:39:35
57	14	1	74	kljlkj;jk	1	07:20:04
19	14	1	26	sdfg;klan;lsf;alskjdflkajs;lkfjas;ld	2	05:39:49
58	14	1	75	1	1	07:20:08
59	14	1	76	2	1	07:20:10
60	14	1	77	3	1	07:20:12
22	14	1	29	editedmessage to edit	2	05:44:20
23	14	1	30	;lkj;lkajf;kljasdf	2	05:44:25
25	14	1	32	;lkja;sldkfja;lskdjf	1	05:44:31
24	14	1	31	klj;akljsfd;akjsdf;kl	2	05:44:35
61	14	1	78		1	07:20:13
62	14	1	79	5	1	07:20:43
63	14	1	80	привет	1	07:22:43
26	14	1	34	;lkjadslfk;ljasdf	2	05:46:00
64	14	1	82		3	07:22:57
65	14	1	83	а вдруг получилось удаление?	1	07:33:13
29	14	1	39		1	05:48:10
28	14	1	38	;lkajsdf;klksldmx	2	05:48:17
66	14	1	85		3	07:38:31
67	14	1	87	ура!!!	2	07:38:41
69	14	1	90		3	07:48:51
68	14	1	91	нет, не фыва	2	07:49:04
70	14	1	92	ф	1	07:49:19
71	14	1	93	ф	1	07:49:20
72	1	1	94	asdf	1	07:51:59
73	1	1	95	asdf	1	07:52:11
74	5	1	96	asdf	1	08:05:25
75	5	1	97	98712309879	1	08:05:33
76	14	1	98	asdf	1	08:05:43
77	14	1	99	asdf	1	09:53:42
78	14	1	100	asdfasdfasdfasdf	1	09:53:47
79	14	1	101	фывафывафывафыва	1	10:05:24
80	14	1	102	афываф	1	10:05:31
81	14	1	103	фыва	1	10:05:57
82	14	1	104	фыва	1	10:05:57
83	14	1	105	фыва	1	10:05:58
84	14	1	106	фы	1	10:05:58
85	14	1	107	ва	1	10:05:59
86	14	1	108	asdf	1	11:55:31
87	14	1	109	asdf	1	11:55:48
88	14	1	110	a;lskfj;klasdf	1	11:55:52
89	14	1	111	ролд	1	10:09:50
90	14	1	112	jhljhlkjhkl	1	10:20:43
91	14	1	113	lkajsdfl;kj	1	12:28:18
92	14	1	114	klas''''''''''''''asd	1	12:28:23
93	14	1	115	asdf''asdf	1	12:28:31
94	14	1	116	asdfas''''''''''''asdf	1	12:28:35
95	14	1	117	''	1	12:28:41
96	14	1	118	''	1	12:29:15
97	14	1	119	''	1	12:31:32
98	14	1	120	''	1	12:31:55
99	14	1	121	'	1	12:36:45
100	14	1	123	asl;kjdf'''	2	12:38:47
101	14	1	124	\\!'"	1	12:45:00
102	14	1	125	"""	1	12:45:04
103	14	1	126	\\\\\\	1	12:45:10
104	14	1	127	alert();	1	04:31:32
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: IFLED
--

COPY users (user_id, name) FROM stdin;
1	Nick
2	Max
3	Harry
5	Jessica
8	Anton Dosov
9	Taylor Swift
10	Lebron James
11	Dasha Pronevich
12	Some Guy
13	test_guy
14	IFLED
15	MintaiDS
16	Trailblazer
\.


--
-- Name: links_pkey; Type: CONSTRAINT; Schema: public; Owner: IFLED; Tablespace: 
--

ALTER TABLE ONLY links
    ADD CONSTRAINT links_pkey PRIMARY KEY (user_id, room_id);


--
-- Name: messages_pkey; Type: CONSTRAINT; Schema: public; Owner: IFLED; Tablespace: 
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (action_id);


--
-- Name: unique_message_id; Type: CONSTRAINT; Schema: public; Owner: IFLED; Tablespace: 
--

ALTER TABLE ONLY messages
    ADD CONSTRAINT unique_message_id UNIQUE (message_id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: IFLED; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

