### postgres text vs varchar
TEXT
Usage: Used for storing large amounts of text data.
Storage: Typically stored outside the main table row, with a pointer to the actual data.
Limit: Can store very large strings (up to 1 GB in PostgreSQL).
Performance: May have different performance characteristics compared to VARCHAR, especially for very large text.

VARCHAR
Usage: Used for storing variable-length strings.
Storage: Stored directly in the table row.
Limit: You can specify a maximum length (e.g., VARCHAR(255)). In PostgreSQL, VARCHAR without a length specifier behaves like TEXT.
Performance: Generally faster for smaller strings and when a length limit is specified.

### Implementing Many to Many

student, likes, course
![img.png](img.png)
in the student class
```java
@ManyToMany
@JoinTable(
  name = "course_like", 
  joinColumns = @JoinColumn(name = "student_id"), 
  inverseJoinColumns = @JoinColumn(name = "course_id"))
Set<Course> likedCourses;
```
in the course class
```java
@ManyToMany(mappedBy = "likedCourses")
Set<Student> likes;
```

More implementation like composite key: https://www.baeldung.com/jpa-many-to-many


axon database setup migrations
this is because on boot axon will look for these tables, if not found, it will throw error.
to let these tables create if you allow ddl-aut: update, it sometime interferes with the existing tables.
```sql
create table dead_letter_entry (
        dead_letter_id varchar(255) not null,
        cause_message varchar(1023),
        cause_type varchar(255),
        diagnostics oid,
        enqueued_at timestamp(6) with time zone not null,
        last_touched timestamp(6) with time zone,
        aggregate_identifier varchar(255),
        event_identifier varchar(255) not null,
        message_type varchar(255) not null,
        meta_data oid,
        payload oid not null,
        payload_revision varchar(255),
        payload_type varchar(255) not null,
        sequence_number bigint,
        time_stamp varchar(255) not null,
        token oid,
        token_type varchar(255),
        type varchar(255),
        processing_group varchar(255) not null,
        processing_started timestamp(6) with time zone,
        sequence_identifier varchar(255) not null,
        sequence_index bigint not null,
        primary key (dead_letter_id)
    )
Hibernate: 
    create table saga_entry (
        saga_id varchar(255) not null,
        revision varchar(255),
        saga_type varchar(255),
        serialized_saga oid,
        primary key (saga_id)
    )
Hibernate: 
    create table token_entry (
        processor_name varchar(255) not null,
        segment integer not null,
        owner varchar(255),
        timestamp varchar(255) not null,
        token oid,
        token_type varchar(255),
        primary key (processor_name, segment)
    )
Hibernate: 
    create index IDXk45eqnxkgd8hpdn6xixn8sgft 
       on association_value_entry (saga_type, association_key, association_value)
Hibernate: 
    create index IDXgv5k1v2mh6frxuy5c0hgbau94 
       on association_value_entry (saga_id, saga_type)
Hibernate: 
    create index IDXe67wcx5fiq9hl4y4qkhlcj9cg 
       on dead_letter_entry (processing_group)
Hibernate: 
    create index IDXrwucpgs6sn93ldgoeh2q9k6bn 
       on dead_letter_entry (processing_group, sequence_identifier)
Hibernate: 
    alter table if exists dead_letter_entry 
       drop constraint if exists UKhlr8io86j74qy298xf720n16v

Hibernate: 
    alter table if exists dead_letter_entry 
       add constraint UKhlr8io86j74qy298xf720n16v unique (processing_group, sequence_identifier, sequence_index)
Hibernate: 
    create sequence association_value_entry_seq start with 1 increment by 50
```