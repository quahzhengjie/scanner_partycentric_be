package com.bkb.scanner.entity;

public enum CustomerLifecycleStatus {
    Prospective,   // Initial contact or lead
    Onboarding,    // Currently in the onboarding process
    Active,        // Fully onboarded active customer
    Dormant,       // Inactive but not closed
    Suspended,     // Temporarily suspended
    Closed,        // Account closed
    Rejected       // Application rejected (e.g., failed AML checks)
}