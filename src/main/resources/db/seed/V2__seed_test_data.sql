-- 학습 환경 전용 시드 데이터. 운영 DB에는 적용 금지.
-- 비밀번호: 모든 계정 'password' (BCrypt strength 10 해시)

INSERT INTO students (student_number, name, grade, email, password) VALUES
                                                                        ('20231001', '김학생', 3, 'student1@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
                                                                        ('20231002', '이학생', 2, 'student2@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

INSERT INTO courses (course_code, course_name, course_type, credits, capacity, current_enrollment) VALUES
                                                                                                       ('CSE2001', '자료구조',     'MAJOR_REQUIRED',   3, 50,  0),
                                                                                                       ('CSE3001', '운영체제',     'MAJOR_REQUIRED',   3, 40,  0),
                                                                                                       ('CSE3002', '데이터베이스',  'MAJOR_ELECTIVE',   3, 30,  0),
                                                                                                       ('GEN1001', '대학영어',     'GENERAL_REQUIRED', 2, 100, 0);