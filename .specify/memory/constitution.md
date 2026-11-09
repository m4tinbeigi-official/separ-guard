# Separ Guard Constitution
<!-- Mobile Security, Anti-Malware & Threat Eradication Suite -->

## Core Principles

### I. Universal Accessibility & Visual-First UX
Every security feature, alert, and flow must be operable without requiring reading ability or technical knowledge. All screens must strictly rely on three universal states: Safe (#00E676 Green), Danger (#FF1744 Crimson), and Attention (#FFC107 Amber). Every critical decision must have an offline, clear Persian voice prompt alternative and a single-tap action.

### II. Zero-Tolerance Threat Eradication & Strict Velora Policy
Any application package containing the substring "velora" (including but not limited to com.velora.cloud) must be automatically flagged as high-severity malware and presented for immediate uninstallation. The engine must actively maintain signatures and package identifiers for all known Iranian banking trojans, SMS worms, fake government apps (Sana, Eblaghieh, Saham Edalat), and unauthorized remote administration tools (RATs).

### III. 100% Offline-First Architecture & Absolute Privacy
The application must execute all scanning, analysis, heuristics, and voice guidance entirely on-device without requiring internet connectivity or uploading telemetry. No user SMS, contact list, device identifier, or personal data may ever leave the device.

### IV. Post-Infection Session Remediation
Malware uninstallation is insufficient on its own. The system must provide direct, visual, three-step deep-link workflows for domestic (Rubika, Eitaa, Bale, Soroush+) and international (Telegram, Instagram, WhatsApp) messaging platforms to terminate all active sessions and eject adversaries.

### V. Persistent 24/7 Autonomous Shielding
Continuous background protection is enforced via three decoupled shields:
1. SMS Guard: Real-time pattern and phishing link detection on incoming SMS.
2. Web & Gateway Guard: Accessibility-based address bar monitoring against spoofed Shaparak portals.
3. Install Guard: Zero-second package install and replacement verification.

### VI. Commercial Proprietary Governance & Sole Ownership
Separ Guard is a strictly commercial, closed-source security asset developed and owned exclusively by Rick Sanchez. The software is not open-source, and all source code, algorithms, visual layouts, and threat databases are fully proprietary. No third-party developer names or trademarked brands may be used.

## Architecture and Quality Standards

### Stack & Platform Requirements
- Language: Modern Kotlin on Android SDK (minSdk 21, targetSdk 35/36).
- UI Framework: Jetpack Compose with Material 3 in high-contrast Dark Mode with strict VibeFarsi RTL layout standards.
- Local Storage: Embedded SQLite/Room database with indexed SHA-256 and package lookup tables.
- Audio Subsystem: Jetpack Media3 / Android MediaPlayer streaming compressed offline OGG/MP3 assets.
- Independence: Rebranded package ir.ricksanchez.separ with zero usage of third-party trademarked names or logos.

## Development Workflow and Quality Gates

- Every threat detection rule must include a verifiable test case.
- False positive prevention: Official system applications and certified domestic banking apps must never be falsely flagged.
- Clean diffs: Minimal code changes, YAGNI compliance, and zero superfluous dependencies.

## Governance

This Constitution supersedes all ad-hoc conventions. Amendments must be versioned semantically. All components and code reviews must verify adherence to accessibility, offline privacy, and proactive eradication principles.

**Version**: 1.0.0 | **Ratified**: 2026-11-04 | **Last Amended**: 2026-11-04
