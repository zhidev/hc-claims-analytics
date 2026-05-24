# Project 2: SQL Optimization & Performance Engineering

This project extends the Healthcare Claims Analytics API by analyzing PostgreSQL query performance on a larger claims dataset.

The goal is to benchmark existing analytics queries using `EXPLAIN ANALYZE`, adding indexes and compare performances before and after optimizations.


## Projects

Project 1 focused on building a Spring Boot REST API for healthcare claims ingestion and analytics.

Project 2 focuses on the database and performance engineering side of the same system.

The same PostgreSQL `claims` table is used for project 2.

## Dataset

Synthetically generated healthcare claims data

Current row count:

```sql
SELECT COUNT(*) AS total_claims
FROM claims;
```

```text
total_claims = 10000
```

## Benchmarkings
The following analytical queries will be benchmarked

1. Claims count by state
2. Claims count by status
3. Total claim amount by state
4. Top provider by claim count

## Methodology
foreach

1. Run the query with ```EXPLAIN ANALYZE```
2. Record the baseline execution plan and time
3. Adding an index 
4. Rerun the query with the index
5. Compare both performance results


## Query 1: Count by State

```sql
EXPLAIN ANALYZE
SELECT state, COUNT(*) AS claim_count
FROM claims
GROUP BY state
ORDER BY claim_count DESC;
```

Before Index:
```text
Sort  (cost=255.20..255.22 rows=8 width=11) (actual time=2.777..2.778 rows=8.00 loops=1)
  Sort Key: (count(*)) DESC
  Sort Method: quicksort  Memory: 25kB
  Buffers: shared hit=108
  ->  HashAggregate  (cost=255.00..255.08 rows=8 width=11) (actual time=2.731..2.737 rows=8.00 loops=1)
        Group Key: state
        Batches: 1  Memory Usage: 32kB
        Buffers: shared hit=105
        ->  Seq Scan on claims  (cost=0.00..205.00 rows=10000 width=3) (actual time=0.023..0.576 rows=10000.00 loops=1)
              Buffers: shared hit=105
Planning:
  Buffers: shared hit=9 read=1
Planning Time: 0.407 ms
Execution Time: 2.867 ms
```

Execution FLow:
```text
Seq Scan on claims -> HashAggregate -> Sort
```
Baseline execution time: 2.867ms to execute this query before indexing.

PostgreSQL scanned the claims table, grouped the rows by state, counted the # of claims per states then sorted the result

## Query 2: Count by Status

```sql
EXPLAIN ANALYZE
SELECT status, COUNT(*) AS claim_count
FROM claims
GROUP BY status
ORDER BY claim_count DESC;
```

Before Index:
```text
Sort  (cost=255.05..255.06 rows=3 width=16) (actual time=3.620..3.621 rows=3.00 loops=1)
  Sort Key: (count(*)) DESC
  Sort Method: quicksort  Memory: 25kB
  Buffers: shared hit=105
  ->  HashAggregate  (cost=255.00..255.03 rows=3 width=16) (actual time=3.383..3.385 rows=3.00 loops=1)
        Group Key: status
        Batches: 1  Memory Usage: 32kB
        Buffers: shared hit=105
        ->  Seq Scan on claims  (cost=0.00..205.00 rows=10000 width=8) (actual time=0.301..0.950 rows=10000.00 loops=1)
              Buffers: shared hit=105
Planning Time: 0.388 ms
Execution Time: 3.780 ms
```

Execution Flow:
```text
Seq Scan on claims -> HashAggregate -> Sort
```

Baseline execution time: 3.780ms to execute this query before indexing

PostgreSQL scanned the claims table, grouped the rows by state, counted the # of claims per status then sorted the result


## Query 3: Total Claim Amount by State

```sql
EXPLAIN ANALYZE
SELECT state, SUM(claim_amount) AS total_claim_amount
FROM claims
GROUP BY state
ORDER BY total_claim_amount DESC;
```

Before Index:
```text
Sort  (cost=255.22..255.24 rows=8 width=35) (actual time=3.732..3.734 rows=8.00 loops=1)
  Sort Key: (sum(claim_amount)) DESC
  Sort Method: quicksort  Memory: 25kB
  Buffers: shared hit=108
  ->  HashAggregate  (cost=255.00..255.10 rows=8 width=35) (actual time=3.542..3.545 rows=8.00 loops=1)
        Group Key: state
        Batches: 1  Memory Usage: 32kB
        Buffers: shared hit=105
        ->  Seq Scan on claims  (cost=0.00..205.00 rows=10000 width=9) (actual time=0.030..0.638 rows=10000.00 loops=1)
              Buffers: shared hit=105
Planning:
  Buffers: shared hit=16 read=5
Planning Time: 1.168 ms
Execution Time: 3.791 ms
```

Execution flow:
```text
Seq Scan on claims -> HashAggregate -> Sort
```



Baseline execution time: 3.791ms to execute before indexing

PostgreSQL scanned the claims table, grouped rows by state, calculated the sum total of claim amount 
per state and sorted the resulting claim amount.

## Query 4: Top Providers by Claim

```sql
EXPLAIN ANALYZE
SELECT provider_id, COUNT(*) AS claim_count
FROM claims
GROUP BY provider_id
ORDER BY claim_count DESC
LIMIT 10;
```

Before Index

```text
Limit  (cost=258.16..258.19 rows=10 width=12) (actual time=3.003..3.006 rows=10.00 loops=1)
  Buffers: shared hit=105
  ->  Sort  (cost=258.16..258.41 rows=100 width=12) (actual time=3.001..3.003 rows=10.00 loops=1)
        Sort Key: (count(*)) DESC
        Sort Method: top-N heapsort  Memory: 25kB
        Buffers: shared hit=105
        ->  HashAggregate  (cost=255.00..256.00 rows=100 width=12) (actual time=2.952..2.966 rows=100.00 loops=1)
              Group Key: provider_id
              Batches: 1  Memory Usage: 32kB
              Buffers: shared hit=105
              ->  Seq Scan on claims  (cost=0.00..205.00 rows=10000 width=4) (actual time=0.019..0.637 rows=10000.00 loops=1)
                    Buffers: shared hit=105
Planning Time: 0.199 ms
Execution Time: 3.069 ms
```
Execution flow:
```text
Seq Scan on claims -> HashAggregate -> Sort -> Limit
```

Baseline execution time: 3.069ms to execute before indexing.

PostgresSQL scans the claims table, group the rows by provider_id, counts the number of claims
for each providers, sorts provider by claim count, and return the top 10 results.

## Baseline Results Summary

| Query | Purpose | Execution Flow | Baseline Time |
|---|---|---|---|
| Query 1 | Count claims by state | Seq Scan -> HashAggregate -> Sort | 2.867 ms |
| Query 2 | Count claims by status | Seq Scan -> HashAggregate -> Sort | 3.780 ms |
| Query 3 | Total claim amount by state | Seq Scan -> HashAggregate -> Sort | 3.791 ms |
| Query 4 | Top providers by claim count | Seq Scan -> HashAggregate -> Sort -> Limit | 3.069 ms |

Presently execution flows are similar prior to indexing.