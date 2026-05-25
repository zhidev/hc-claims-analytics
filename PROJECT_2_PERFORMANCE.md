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



After Index:
```sql
CREATE INDEX idx_claims_state
ON claims(state);
```

Running the same sql cmd
```sql
EXPLAIN ANALYZE
SELECT state, COUNT(*) AS claim_count
FROM claims
GROUP BY state
ORDER BY claim_count DESC;
```

```text
Sort  (cost=244.49..244.51 rows=8 width=11) (actual time=2.084..2.086 rows=8.00 loops=1)
  Sort Key: (count(*)) DESC
  Sort Method: quicksort  Memory: 25kB
  Buffers: shared hit=1 read=10
  ->  GroupAggregate  (cost=0.29..244.37 rows=8 width=11) (actual time=0.395..2.067 rows=8.00 loops=1)
        Group Key: state
        Buffers: shared hit=1 read=10
        ->  Index Only Scan using idx_claims_state on claims  (cost=0.29..194.28 rows=10000 width=3) (actual time=0.155..1.097 rows=10000.00 loops=1)
              Heap Fetches: 0
              Index Searches: 1
              Buffers: shared hit=1 read=10
Planning:
  Buffers: shared hit=19 read=1
Planning Time: 0.512 ms
Execution Time: 2.135 ms
```
Execution flow becomes
```text
Index Only Scan using idx_claims_state -> GroupAggregate -> Sort
```


### Comparison

| Metric | Before Index | After Index |
|---|---:|---:|
| Execution Time | 2.867 ms | 2.135 ms |
| Scan Type | Seq Scan | Index Only Scan |
| Aggregation Type | HashAggregate | GroupAggregate |
| Index Used | No | Yes — `idx_claims_state` |
| Notes | Full table scan | `Heap Fetches: 0` |

## Query 1 Analysis
Before indexing, PostgreSQL perform sequential scan on the claims table
iterating through all 10,000 rows before grouping the results by states.

After indexing, PostgreSQL used an Index Only Scan with idx_claims_state.
Because the query only requires the state column and row count, it was 
able to answer the query directly from index.

The report also reported Heap Fetches: 0 , which means it grabbed 0 rows
from the main table. 

The execution time of using index for the query improved from
2.867ms to 2.135ms, an improvement of 2.135/2.867 = ~25.5%.



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




After Index:
```sql
CREATE INDEX idx_claims_status
ON claims(status);
```

Running the same sql cmd
```sql
EXPLAIN ANALYZE
SELECT status, COUNT(*) AS claim_count
FROM claims
GROUP BY status
ORDER BY claim_count DESC;
```
```text
Sort  (cost=255.05..255.06 rows=3 width=16) (actual time=3.131..3.132 rows=3.00 loops=1)
  Sort Key: (count(*)) DESC
  Sort Method: quicksort  Memory: 25kB
  Buffers: shared hit=108
  ->  HashAggregate  (cost=255.00..255.03 rows=3 width=16) (actual time=3.096..3.103 rows=3.00 loops=1)
        Group Key: status
        Batches: 1  Memory Usage: 32kB
        Buffers: shared hit=105
        ->  Seq Scan on claims  (cost=0.00..205.00 rows=10000 width=8) (actual time=0.025..0.627 rows=10000.00 loops=1)
              Buffers: shared hit=105
Planning:
  Buffers: shared hit=10
Planning Time: 0.256 ms
Execution Time: 3.211 ms
```

Execution flow remains UNCHANGED
```text
Seq Scan on claims -> HashAggregate -> Sort
```
### Comparison



| Metric | Before Index | After Index |
|---|---:|---:|
| Execution Time | 3.780 ms | 3.211 ms |
| Scan Type | Seq Scan | Seq Scan |
| Aggregation Type | HashAggregate | HashAggregate |
| Index Used | No | No |
| Notes | Baseline sequential scan | Index created, but planner did not use it |



## Query 2 Analysis

After adding an index on status, PostgreSQL continued to use the same execution flow, despite
being very similar to Query 1.

This is potentially because status has low cardinality, producing only a few grouped values: 
```
APPROVED
DENIED
PENDING
```

This also shows that even if we add an index, PostgreSQL won't necessarily 
use the created index because it has decided using its initial plan. Which implies that using index 
overhead doesn't outweigh the benefits. Because the execution flow is similar
the runtime execution is due to runtime variance.


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

After Index:
This uses the same index in Query 1.

sql cmd
```sql
EXPLAIN ANALYZE
SELECT state, SUM(claim_amount) AS total_claim_amount
FROM claims
GROUP BY state
ORDER BY total_claim_amount DESC;
```
```text
Sort  (cost=255.22..255.24 rows=8 width=35) (actual time=3.628..3.630 rows=8.00 loops=1)
  Sort Key: (sum(claim_amount)) DESC
  Sort Method: quicksort  Memory: 25kB
  Buffers: shared hit=108
  ->  HashAggregate  (cost=255.00..255.10 rows=8 width=35) (actual time=3.577..3.581 rows=8.00 loops=1)
        Group Key: state
        Batches: 1  Memory Usage: 32kB
        Buffers: shared hit=105
        ->  Seq Scan on claims  (cost=0.00..205.00 rows=10000 width=9) (actual time=0.016..0.701 rows=10000.00 loops=1)
              Buffers: shared hit=105
Planning:
  Buffers: shared hit=21
Planning Time: 0.259 ms
Execution Time: 3.689 ms
```
Execution flow remains UNCHANGED
```text
Seq Scan -> HashAggregate -> Sort
```

| Metric           |             Before Index |                         After Index |
| ---------------- | -----------------------: | ----------------------------------: |
| Execution Time   |                 3.791 ms |                            3.689 ms |
| Scan Type        |                 Seq Scan |                            Seq Scan |
| Aggregation Type |            HashAggregate |                       HashAggregate |
| Index Used       |                       No |                                  No |
| Notes            | Baseline sequential scan | Existing `state` index was not used |

## Query 3 Analysis
Despite also using state similarly to query 1, we still used a HashAggregate execution
flow. The difference here is that it must also sum the claim amount. Our previously 
created index only contains state mapping, and evidently it was not enough 
for PostgreSQL to fully answer the query from the index and elected the original
execution flow was better.

The variance in execution time is minimal enough to be attributed to various runtime factors rather
than any direct improvement. 


## Query 3.b
We will create a new index with claim_amounts
After new index:
```sql
CREATE INDEX idx_claims_state_claim_amount
ON claims(state, claim_amount);
```

```sql
EXPLAIN ANALYZE
SELECT state, SUM(claim_amount) AS total_claim_amount
FROM claims
GROUP BY state
ORDER BY total_claim_amount DESC
```
```text
Sort  (cost=255.22..255.24 rows=8 width=35) (actual time=3.493..3.494 rows=8.00 loops=1)
  Sort Key: (sum(claim_amount)) DESC
  Sort Method: quicksort  Memory: 25kB
  Buffers: shared hit=105
  ->  HashAggregate  (cost=255.00..255.10 rows=8 width=35) (actual time=3.435..3.439 rows=8.00 loops=1)
        Group Key: state
        Batches: 1  Memory Usage: 32kB
        Buffers: shared hit=105
        ->  Seq Scan on claims  (cost=0.00..205.00 rows=10000 width=9) (actual time=0.021..0.594 rows=10000.00 loops=1)
              Buffers: shared hit=105
Planning:
  Buffers: shared hit=23 read=1
Planning Time: 0.578 ms
Execution Time: 3.546 ms
```
Execution flow remains UNCHANGED
```text
Seq Scan -> HashAggregate -> Sort
```

| Metric           |             Before Index |               After State Index |                    After State + Claim Index |
| ---------------- | -----------------------: | ------------------------------: |---------------------------------------------:|
| Execution Time   |                 3.791 ms |                        3.689 ms |                                     3.546 ms |
| Scan Type        |                 Seq Scan |                        Seq Scan |                                     Seq Scan |
| Aggregation Type |            HashAggregate |                   HashAggregate |                                HashAggregate |
| Index Used       |                       No |                              No |                                           No |
| Notes            | Baseline sequential scan | `idx_claims_state` was not used | `idx_claims_state_claim_amount` was not used |

An extra index was created to test the hypothesis of it using a new index that
covers state and claim_amount. However, execution flow is still the same.

Execution time is still similar to baseline because the execution flow is still the same plan.

This result shows that even with a tailored index, PostgreSQL may still not use said index.
The query needs to be validated via EXPLAIN ANALYZE. PostgeSQL must still evaluate that using the index
is cheaper than the sequential scan plan.

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

After index:
```sql
CREATE INDEX idx_claims_provider_id
ON claims(provider_id);
```

```sql
EXPLAIN ANALYZE
SELECT provider_id, COUNT(*) AS claim_count
FROM claims
GROUP BY provider_id
ORDER BY claim_count DESC
LIMIT 10;
```

```text
Limit  (cost=247.45..247.47 rows=10 width=12) (actual time=2.150..2.153 rows=10.00 loops=1)
  Buffers: shared hit=1 read=10
  ->  Sort  (cost=247.45..247.70 rows=100 width=12) (actual time=2.148..2.149 rows=10.00 loops=1)
        Sort Key: (count(*)) DESC
        Sort Method: top-N heapsort  Memory: 25kB
        Buffers: shared hit=1 read=10
        ->  GroupAggregate  (cost=0.29..245.28 rows=100 width=12) (actual time=0.172..2.078 rows=100.00 loops=1)
              Group Key: provider_id
              Buffers: shared hit=1 read=10
              ->  Index Only Scan using idx_claims_provider_id on claims  (cost=0.29..194.28 rows=10000 width=4) (actual time=0.147..1.051 rows=10000.00 loops=1)
                    Heap Fetches: 0
                    Index Searches: 1
                    Buffers: shared hit=1 read=10
Planning:
  Buffers: shared hit=20 read=1
Planning Time: 0.706 ms
Execution Time: 2.225 ms
```
Execution flow changed to:
```text
Index Only Scan using idx_claims_provider_id -> GroupAggregate -> Sort -> Limit
```

| Metric           |             Before Index |                    After Index |
| ---------------- | -----------------------: | -----------------------------: |
| Execution Time   |                 3.069 ms |                       2.225 ms |
| Scan Type        |                 Seq Scan |                Index Only Scan |
| Aggregation Type |            HashAggregate |                 GroupAggregate |
| Index Used       |                       No | Yes — `idx_claims_provider_id` |
| Notes            | Baseline sequential scan |              `Heap Fetches: 0` |

For this final query we see PostgresSQL uses index search with 0 heap searches meaning
it didn't do any lookup on the original table as opposed to the baseline sequential scan.
Execution time improved from 3.069ms to 2.225ms, a roughly 27.5% improvement. 

This result shows that indexes can be especially useful for grouped 
COUNT(*) queries when the grouped column is available directly 
from the index and PostgreSQL can directly count index entries 
using an index-only scan.

## Baseline Results Summary

| Query | Purpose | Execution Flow | Baseline Time |
|---|---|---|---|
| Query 1 | Count claims by state | Seq Scan -> HashAggregate -> Sort | 2.867 ms |
| Query 2 | Count claims by status | Seq Scan -> HashAggregate -> Sort | 3.780 ms |
| Query 3 | Total claim amount by state | Seq Scan -> HashAggregate -> Sort | 3.791 ms |
| Query 4 | Top providers by claim count | Seq Scan -> HashAggregate -> Sort -> Limit | 3.069 ms |

Presently execution flows are similar prior to indexing.

## Post Index Resultss Summary

## Final Results Summary

| Query | Index Tested | Index Used? | Before Time | After Time | Result |
|---|---|---:|---:|---:|---|
| Count by State | `idx_claims_state` | Yes | 2.867 ms | 2.135 ms | Improved |
| Count by Status | `idx_claims_status` | No | 3.780 ms | 3.211 ms | Plan unchanged |
| Total Claim Amount by State | `idx_claims_state`, `idx_claims_state_claim_amount` | No | 3.791 ms | 3.546 ms | Plan unchanged |
| Top Providers by Claim Count | `idx_claims_provider_id` | Yes | 3.069 ms | 2.225 ms | Improved 

This project tested PostgreSQL indexing behaviors with analytical queries in Project 1.

The indexes on `state` and `provider_id` were used for grouped 
`count(*)` queries, allowing PostgreSQL to perform index-only scans.
In both cases, the execution plan changed from a sequential scan to an index-only scan, and execution time improved.

The index for `status` was unused, most likely because status has very low cardinality with only few distinct values 
and the overhead of using an index didn't outweigh the sequential scan of the table.

For the total claim amount by state query, both a state-only index and a composite index on 
`(state, claim_amount)` were tested. PostgreSQL still chose a sequential scan, showing that even 
a composite index does not guarantee index usage when PostgreSQL estimates that scanning the 
table sequentially is cheaper.

The main takeaway is that indexing strategy depends on how the query uses the data. In this dataset, 
indexes helped most when PostgreSQL could count grouped index entries directly without
fetching rows from the main table.
