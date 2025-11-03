#!/bin/bash

set -e

# 모듈별 JaCoCo 리포트를 수집하여 Markdown 테이블 생성
# Usage: ./generate-coverage-summary.sh

# 커버리지를 수집할 모듈 목록
MODULES=(
  "threadly-core/core-domain"
  "threadly-core/core-service"
  "threadly-commons"
  "threadly-adapters/adapter-persistence"
  "threadly-adapters/adapter-redis"
  "threadly-adapters/adapter-storage"
  "threadly-adapters/adapter-kafka"
  "threadly-apps/app-api"
  "threadly-apps/app-batch"
)

echo "## Code Coverage Report (By Module)"
echo ""
echo "| Module | Instruction | Branch | Line | Method | Class |"
echo "|--------|-------------|--------|------|--------|-------|"

for module in "${MODULES[@]}"; do
  XML_FILE="$module/build/reports/jacoco/test/jacocoTestReport.xml"

  if [ ! -f "$XML_FILE" ]; then
    continue
  fi

  # 모듈 이름 추출 (마지막 부분만)
  MODULE_NAME=$(basename "$module")

  # XML에서 report-level counter 추출
  COUNTERS=$(xmllint --format "$XML_FILE" 2>/dev/null | tail -8 | head -7 | grep '<counter')

  # 각 메트릭별 커버리지 계산
  INSTRUCTION_PERCENT=""
  BRANCH_PERCENT=""
  LINE_PERCENT=""
  METHOD_PERCENT=""
  CLASS_PERCENT=""

  while IFS= read -r line; do
    TYPE=$(echo "$line" | sed -n 's/.*type="\([^"]*\)".*/\1/p')
    MISSED=$(echo "$line" | sed -n 's/.*missed="\([^"]*\)".*/\1/p')
    COVERED=$(echo "$line" | sed -n 's/.*covered="\([^"]*\)".*/\1/p')
    TOTAL=$((MISSED + COVERED))

    if [ $TOTAL -gt 0 ]; then
      PERCENT=$(awk "BEGIN {printf \"%.1f\", ($COVERED * 100.0 / $TOTAL)}")

      case $TYPE in
        INSTRUCTION) INSTRUCTION_PERCENT="${PERCENT}%" ;;
        BRANCH) BRANCH_PERCENT="${PERCENT}%" ;;
        LINE) LINE_PERCENT="${PERCENT}%" ;;
        METHOD) METHOD_PERCENT="${PERCENT}%" ;;
        CLASS) CLASS_PERCENT="${PERCENT}%" ;;
      esac
    fi
  done <<< "$COUNTERS"

  # 테이블 행 출력
  echo "| $MODULE_NAME | $INSTRUCTION_PERCENT | $BRANCH_PERCENT | $LINE_PERCENT | $METHOD_PERCENT | $CLASS_PERCENT |"
done

echo ""
echo "Generated: $(date '+%Y-%m-%d %H:%M:%S')"
