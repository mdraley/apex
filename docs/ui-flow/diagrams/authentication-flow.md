# Authentication Flow Diagram

This diagram shows the complete authentication and authorization flow for all user types in the Apex IDP platform.

## Authentication Flow

```mermaid
graph TD
    A[User Access Application] --> B{Already Authenticated?}
    B -->|Yes| C[Check Session Validity]
    B -->|No| D[Display Login Page]

    C --> E{Session Valid?}
    E -->|Yes| F[Redirect to Role Dashboard]
    E -->|No| D

    D --> G[User Enters Credentials]
    G --> H[Submit Login Form]
    H --> I[Validate Credentials]

    I --> J{Credentials Valid?}
    J -->|No| K[Display Error Message]
    K --> D
    J -->|Yes| L[Generate JWT Token]

    L --> M[Extract User Role]
    M --> N{Role Type}

    N -->|AP Clerk| O[Validation Dashboard]
    N -->|CFO| P[Executive Dashboard]
    N -->|IT Admin| Q[Admin Portal]
    N -->|Vendor| R[Vendor Portal]

    O --> S[Load Pending Documents]
    P --> T[Load Financial Metrics]
    Q --> U[Load System Status]
    R --> V[Load Vendor Documents]

    S --> W[Ready for Validation Work]
    T --> X[Ready for Executive Review]
    U --> Y[Ready for Administration]
    V --> Z[Ready for Vendor Actions]

    classDef userAction fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef process fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef endpoint fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000

    class A,G,H userAction
    class B,E,J,N decision
    class C,I,L,M process
    class W,X,Y,Z endpoint
```

## Role-Based Access Control

```mermaid
graph TD
    A[JWT Token] --> B[Extract Claims]
    B --> C[Get User Role]

    C --> D{Role Check}
    D -->|AP_CLERK| E[Validation Permissions]
    D -->|CFO| F[Executive Permissions]
    D -->|IT_ADMIN| G[Admin Permissions]
    D -->|VENDOR| H[Vendor Permissions]

    E --> I[- View validation queue<br/>- Approve/reject documents<br/>- Add comments<br/>- Export reports]
    F --> J[- View all dashboards<br/>- Access financial data<br/>- Generate reports<br/>- View audit logs]
    G --> K[- Manage users<br/>- Configure system<br/>- View all data<br/>- Manage integrations]
    H --> L[- Submit invoices<br/>- Track payments<br/>- Update profile<br/>- View own documents]

    I --> M[Redirect to /validation]
    J --> N[Redirect to /dashboard]
    K --> O[Redirect to /admin]
    L --> P[Redirect to /vendor]

    classDef token fill:#6C5CE7,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef role fill:#00B894,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef permissions fill:#FDCB6E,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef redirect fill:#E17055,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class A,B,C token
    class D,E,F,G,H role
    class I,J,K,L permissions
    class M,N,O,P redirect
```

## Session Management

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant B as Backend
    participant R as Redis

    U->>F: Login Request
    F->>B: POST /api/auth/login
    B->>B: Validate Credentials
    B->>B: Generate JWT Token
    B->>R: Store Session Data
    B->>F: Return JWT + User Info
    F->>F: Store JWT in Memory
    F->>U: Redirect to Dashboard

    Note over F,B: Subsequent Requests
    U->>F: Navigate to Protected Page
    F->>B: API Request with JWT
    B->>B: Validate JWT
    B->>R: Check Session Status
    R->>B: Session Valid
    B->>F: Return Protected Data
    F->>U: Display Page Content

    Note over F,R: Token Refresh
    F->>B: Request Token Refresh
    B->>R: Validate Refresh Token
    R->>B: Valid Refresh Token
    B->>B: Generate New JWT
    B->>F: Return New JWT

    Note over U,R: Logout
    U->>F: Logout Request
    F->>B: POST /api/auth/logout
    B->>R: Invalidate Session
    B->>F: Logout Confirmation
    F->>F: Clear JWT from Memory
    F->>U: Redirect to Login
```

## Multi-Factor Authentication (Future Enhancement)

```mermaid
graph TD
    A[User Login] --> B[Primary Authentication]
    B --> C{Primary Auth Success?}
    C -->|No| D[Display Error]
    C -->|Yes| E[Check MFA Requirement]

    E --> F{MFA Required?}
    F -->|No| G[Generate Session]
    F -->|Yes| H[Request MFA Method]

    H --> I{MFA Method}
    I -->|SMS| J[Send SMS Code]
    I -->|Email| K[Send Email Code]
    I -->|Authenticator| L[Request TOTP]
    I -->|Biometric| M[Request Biometric]

    J --> N[User Enters SMS Code]
    K --> O[User Enters Email Code]
    L --> P[User Enters TOTP]
    M --> Q[Biometric Verification]

    N --> R[Validate SMS Code]
    O --> S[Validate Email Code]
    P --> T[Validate TOTP]
    Q --> U[Validate Biometric]

    R --> V{Valid?}
    S --> V
    T --> V
    U --> V

    V -->|No| W[MFA Failed - Retry]
    V -->|Yes| G

    W --> X{Retry Count}
    X -->|< 3| H
    X -->|>= 3| Y[Account Locked]

    G --> Z[User Authenticated]

    classDef primary fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef mfa fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef success fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef failure fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class A,B,G,Z primary
    class H,I,J,K,L,M,N,O,P,Q,R,S,T,U mfa
    class G,Z success
    class D,W,Y failure
```

## Security Considerations

### Token Security

- JWT tokens are stored in memory only (not localStorage)
- Tokens have short expiration times (15 minutes)
- Refresh tokens are used for session extension
- All tokens are invalidated on logout

### Password Security

- Passwords are hashed using bcrypt with salt
- Password complexity requirements enforced
- Account lockout after failed attempts
- Password reset via secure email tokens

### Session Security

- Sessions are stored in Redis with TTL
- Session data includes IP address validation
- Concurrent session limits per user
- Automatic session cleanup

---

## Dark Mode Color Legend

The following color scheme has been optimized for dark backgrounds with high contrast and accessibility in mind:

```mermaid
graph TD
    A[User Action] --> B{Decision Point}
    B --> C[System Process]
    C --> D[Success/Endpoint]
    D --> E[Authentication Token]
    E --> F[Role Assignment]
    F --> G[Permission Set]
    G --> H[Redirect Action]
    H --> I[MFA Process]
    I --> J[Error State]

    classDef userAction fill:#4A90E2,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FF9500,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef process fill:#9013FE,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef endpoint fill:#50E3C2,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef token fill:#6C5CE7,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef role fill:#00B894,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef permissions fill:#FDCB6E,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef redirect fill:#E17055,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF
    classDef mfa fill:#F5A623,stroke:#FFFFFF,stroke-width:2px,color:#000000
    classDef failure fill:#D0021B,stroke:#FFFFFF,stroke-width:2px,color:#FFFFFF

    class A userAction
    class B decision
    class C process
    class D endpoint
    class E token
    class F role
    class G permissions
    class H redirect
    class I mfa
    class J failure
```

### Authentication Flow Color Code Reference:

| Color         | Hex Code  | Purpose                    | Text Color | Usage Context                            |
| ------------- | --------- | -------------------------- | ---------- | ---------------------------------------- |
| 🔵 **Blue**   | `#4A90E2` | User Actions & Input       | White      | Login forms, user interactions           |
| 🟠 **Orange** | `#FF9500` | Decision Points            | Black      | Authentication checks, role routing      |
| 🟣 **Purple** | `#9013FE` | System Processing          | White      | Token generation, validation             |
| 🔷 **Teal**   | `#50E3C2` | Success States & Endpoints | Black      | Successful completion, ready states      |
| 🟣 **Indigo** | `#6C5CE7` | Authentication Tokens      | White      | JWT handling, token operations           |
| 🟢 **Green**  | `#00B894` | Role Assignment            | White      | User role determination                  |
| 🟡 **Yellow** | `#FDCB6E` | Permission Sets            | Black      | Permission definitions, access rights    |
| 🔸 **Coral**  | `#E17055` | Redirect Actions           | White      | Navigation, routing operations           |
| 🟤 **Amber**  | `#F5A623` | MFA Processes              | Black      | Multi-factor authentication steps        |
| 🔴 **Red**    | `#D0021B` | Error States               | White      | Authentication failures, security issues |

### Authentication-Specific Design Principles:

- **Security Focus**: Red for errors emphasizes security concerns
- **Process Flow**: Purple for system processes maintains consistency with other diagrams
- **User Clarity**: Blue for user actions provides clear interaction points
- **Role Distinction**: Separate colors for roles, permissions, and redirects for clarity
- **MFA Emphasis**: Distinct amber color for multi-factor authentication processes
- **High Contrast**: All colors provide excellent visibility on dark backgrounds
- **Accessibility**: Compliant with WCAG 2.1 AA standards for color contrast

_This diagram is part of the Apex IDP UI Flow Documentation_
_Last updated: [Current Date]_
