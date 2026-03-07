#!/bin/bash

# Script to start Android emulator
# Usage: ./start-emulator.sh [AVD_NAME]

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'  # No Color

# Default AVD name
DEFAULT_AVD="Pixel_5"

# Get AVD name from parameter or use default
AVD_NAME=${1:-$DEFAULT_AVD}

echo -e "${YELLOW}🚀 Starting Android Emulator...${NC}"
echo -e "${YELLOW}AVD: ${AVD_NAME}${NC}"

# Check if ANDROID_HOME is set
if [ -z "$ANDROID_HOME" ]; then
  echo -e "${RED}❌ ANDROID_HOME environment variable is not set${NC}"
  echo "Please set ANDROID_HOME to your Android SDK path"
  exit 1
fi

# Check if emulator executable exists
EMULATOR_PATH="$ANDROID_HOME/emulator/emulator"
if [ ! -f "$EMULATOR_PATH" ]; then
  echo -e "${RED}❌ Emulator executable not found at: $EMULATOR_PATH${NC}"
  echo "Please check your ANDROID_HOME path"
  exit 1
fi

# Check if emulator is already running
if adb devices | grep -q "emulator.*device"; then
  echo -e "${YELLOW}⚠️  Emulator is already running${NC}"
  adb devices
  exit 0
fi

# List available AVDs
echo -e "${YELLOW}📱 Available AVDs:${NC}"
"$EMULATOR_PATH" -list-avds

# Check if specified AVD exists
if ! "$EMULATOR_PATH" -list-avds | grep -q "^$AVD_NAME$"; then
  echo -e "${RED}❌ AVD '$AVD_NAME' not found${NC}"
  echo "Available AVDs:"
  "$EMULATOR_PATH" -list-avds
  exit 1
fi

# Start the emulator with additional flags to avoid popups
echo -e "${GREEN}▶️  Starting emulator: $AVD_NAME${NC}"
"$EMULATOR_PATH" -avd "$AVD_NAME" -no-audio -no-boot-anim -no-snapshot-save -no-snapshot &

# Wait for emulator to appear in adb devices
echo -e "${YELLOW}⏳ Waiting for emulator to start...${NC}"
MAX_WAIT=120  # 2 minutes
WAIT_TIME=0

while [ $WAIT_TIME -lt $MAX_WAIT ]; do
  if adb devices | grep -q "emulator.*device"; then
    break
  fi
  sleep 5
  WAIT_TIME=$((WAIT_TIME + 5))
  echo -e "${YELLOW}⏳ Still waiting... (${WAIT_TIME}s)${NC}"
done

if ! adb devices | grep -q "emulator.*device"; then
  echo -e "${RED}❌ Emulator failed to start within ${MAX_WAIT} seconds${NC}"
  exit 1
fi

EMU_SERIAL=$(adb devices | grep 'emulator.*device' | head -1 | awk '{print $1}')

# Wait until emulator reports boot completed
echo -e "${YELLOW}⏳ Waiting for emulator to fully boot...${NC}"
BOOT_TIMEOUT=300
BOOT_WAIT=0
BOOT_STATUS=""

while [ $BOOT_WAIT -lt $BOOT_TIMEOUT ]; do
  BOOT_STATUS=$(adb -s "$EMU_SERIAL" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
  if [ "$BOOT_STATUS" = "1" ]; then
    echo -e "${GREEN}✅ Emulator fully booted${NC}"
    break
  fi
  sleep 5
  BOOT_WAIT=$((BOOT_WAIT + 5))
done

if [ "$BOOT_STATUS" != "1" ]; then
  echo -e "${RED}❌ Emulator failed to boot within $BOOT_TIMEOUT seconds${NC}"
  exit 1
fi

# Install Appium Settings APK
SETTINGS_APK_DIR="$HOME/.appium/node_modules/appium-uiautomator2-driver/node_modules/io.appium.settings/apks"
SETTINGS_APK=$(find "$SETTINGS_APK_DIR" -maxdepth 1 -name 'settings_apk*.apk' 2>/dev/null | head -1)

if [ -f "$SETTINGS_APK" ]; then
  echo -e "${YELLOW}📦 Installing Appium Settings APK...${NC}"
  adb -s "$EMU_SERIAL" install -r "$SETTINGS_APK"
  echo -e "${YELLOW}🔑 Granting necessary permissions to Appium Settings...${NC}"
  adb -s "$EMU_SERIAL" shell pm grant io.appium.settings android.permission.WRITE_SECURE_SETTINGS 2>/dev/null || true
  adb -s "$EMU_SERIAL" shell pm grant io.appium.settings android.permission.SET_ANIMATION_SCALE 2>/dev/null || true
  adb -s "$EMU_SERIAL" shell pm grant io.appium.settings android.permission.CHANGE_CONFIGURATION 2>/dev/null || true
  adb -s "$EMU_SERIAL" shell pm grant io.appium.settings android.permission.ACCESS_FINE_LOCATION 2>/dev/null || true
  echo -e "${GREEN}✅ Appium Settings installed and permissions granted${NC}"
else
  echo -e "${RED}❌ Appium Settings APK not found at $SETTINGS_APK_DIR${NC}"
fi

# Wait additional 5 seconds just to be safe
echo -e "${YELLOW}⏳ Waiting an extra 5 seconds for stability...${NC}"
sleep 2

echo -e "${GREEN}✅ Emulator ready${NC}"
adb devices
exit 0
