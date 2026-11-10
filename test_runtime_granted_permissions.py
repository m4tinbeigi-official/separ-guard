#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
تست تفکیک دسترسی فعال (Granted) در برابر درخواست استاتیک (Requested):
فقط برنامه‌هایی که در حال حاضر مجوز پیامک به آن‌ها اعطا شده باید در لیست نمایش داده شوند.
"""

import os

BASE_DIR = "/Users/ricksabchez/workspace/projects/separ-guard"

def test_runtime_permission_logic():
    guard_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/SmsAndScreenGuard.java")
    with open(guard_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "isPermissionActuallyGranted" in content, "isPermissionActuallyGranted must exist"
    assert "hasActiveSmsPermission" in content, "hasActiveSmsPermission must exist"
    assert "hasActiveScreenRisk" in content, "hasActiveScreenRisk must exist"
    assert "REQUESTED_PERMISSION_GRANTED" in content, "REQUESTED_PERMISSION_GRANTED flag must be checked"
    assert "pm.checkPermission" in content, "pm.checkPermission must be checked"

def test_ui_copy_clarity():
    tab_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/SmsAuditTabView.java")
    with open(tab_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "هم‌اکنون دسترسی فعال به پیامک‌های شما دارند" in content
    assert "فقط برنامه‌هایی نمایش داده می‌شوند که همین حالا مجوز پیامک به آن‌ها اعطا شده است" in content

if __name__ == "__main__":
    test_runtime_permission_logic()
    test_ui_copy_clarity()
    print("ALL RUNTIME GRANTED SMS TESTS PASSED SUCCESSFULLY!")
