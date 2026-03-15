#!/bin/bash

# Script to stop Android emulator
# Usage: ./stop-emulator.sh

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'  # No Color

echo -e "${YELLOW}🛑 Stopping Android Emulator...${NC}"

# Check if emulator is running
if ! adb devices | grep -q "emulator.*device"; then
  echo -e "${YELLOW}⚠️  No emulator is currently running${NC}"
  exit 0
fi

# Get emulator device ID
EMULATOR_DEVICE=$(adb devices | grep "emulator.*device" | awk '{print $1}')

if [ -z "$EMULATOR_DEVICE" ]; then
  echo -e "${YELLOW}⚠️  No emulator device found${NC}"
  exit 0
fi

echo -e "${YELLOW}📱 Found emulator: ${EMULATOR_DEVICE}${NC}"

# Stop the emulator
echo -e "${GREEN}▶️  Stopping emulator...${NC}"
adb -s "$EMULATOR_DEVICE" emu kill

# Wait for emulator to stop
echo -e "${YELLOW}⏳ Waiting for emulator to stop...${NC}"
MAX_WAIT=30   # seconds
WAIT_TIME=0

while [ $WAIT_TIME -lt $MAX_WAIT ]; do
  if ! adb devices | grep -q "emulator"; then
    echo -e "${GREEN}✅ Emulator stopped successfully!${NC}"
    adb devices
    exit 0
  fi

  sleep 2
  WAIT_TIME=$((WAIT_TIME + 2))
  echo -e "${YELLOW}⏳ Still stopping... (${WAIT_TIME}s)${NC}"
done

echo -e "${RED}❌ Emulator failed to stop within ${MAX_WAIT} seconds${NC}"
echo -e "${YELLOW}💡 You may need to stop it manually${NC}"
exit 1
