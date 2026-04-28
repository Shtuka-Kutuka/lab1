# JMeter Load Test Guide

This directory contains load test artifacts for asynchronous tasks and concurrency demo APIs.

## Prerequisites

- Running application on `http://localhost:8080`
- Apache JMeter 5.6+ installed

## Test Plan

- File: `DailyMoodtracker-async-load.jmx`
- Covered endpoints:
  - `POST /api/async-tasks/start`
  - `GET /api/async-tasks/{taskId}` (sample placeholder call)
  - `POST /api/concurrency/race-demo/compare`
  - `POST /api/concurrency/counter/increment`

## Suggested Run

1. Open the `.jmx` file in JMeter.
2. Set Thread Group:
   - Threads: 100
   - Ramp-up: 10 sec
   - Loop count: 20
3. Run test and open Summary Report.
4. Export report as CSV and place it in this folder.

## Result Template

Create or update `RESULTS.md` with:

- Test date/time
- Environment (CPU, RAM, JVM version)
- Thread settings
- Throughput
- Avg / P95 / P99 response time
- Error %
- Observations
