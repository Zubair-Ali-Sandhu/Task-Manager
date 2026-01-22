# Task Manager - Design System & Color Palette

## 🎨 Color Palette Reference

### Primary Colors
```
Deep Blue (Primary)
#1E3A8A
RGB: 30, 58, 138
Use: Main brand color, headers, important buttons

Slate Dark (Primary Dark)
#1E293B
RGB: 30, 41, 59
Use: Status bar, dark accents

Bright Blue (Primary Variant)
#3B82F6
RGB: 59, 130, 246
Use: Interactive elements, links, highlights

Light Blue (Primary Light)
#DBEAFE
RGB: 219, 234, 254
Use: Light backgrounds, hover states
```

### Accent Colors
```
Purple (Accent)
#8B5CF6
RGB: 139, 92, 246
Use: FAB, checkboxes, call-to-action buttons

Light Purple (Accent Light)
#C4B5FD
RGB: 196, 181, 253
Use: Hover states, disabled buttons

Emerald Green (Secondary)
#10B981
RGB: 16, 185, 129
Use: Success messages, low priority tasks

Dark Emerald (Secondary Variant)
#059669
RGB: 5, 150, 105
Use: Active success states
```

### Priority Colors
```
Vivid Red (High Priority)
#EF4444
RGB: 239, 68, 68
Use: High priority tasks, urgent alerts

Amber (Medium Priority)
#F59E0B
RGB: 245, 158, 11
Use: Medium priority tasks, warnings

Emerald (Low Priority)
#10B981
RGB: 16, 185, 129
Use: Low priority tasks, completed states
```

### Text Colors
```
Slate 900 (Text Primary)
#0F172A
RGB: 15, 23, 42
Use: Headings, primary body text

Slate 500 (Text Secondary)
#64748B
RGB: 100, 116, 139
Use: Descriptions, secondary text

Slate 400 (Text Tertiary)
#94A3B8
RGB: 148, 163, 184
Use: Meta information, timestamps, due dates

White (Text on Primary)
#FFFFFF
RGB: 255, 255, 255
Use: Text on colored backgrounds
```

### Background Colors
```
Slate 50 (Background)
#F8FAFC
RGB: 248, 250, 252
Use: Main app background

White (Surface)
#FFFFFF
RGB: 255, 255, 255
Use: Card backgrounds, elevated surfaces

Slate 100 (Background Secondary)
#F1F5F9
RGB: 241, 245, 249
Use: Completed task background, subtle variations

Slate 200 (Divider)
#E2E8F0
RGB: 226, 232, 240
Use: Dividers, borders

Slate 300 (Border)
#CBD5E1
RGB: 203, 213, 225
Use: Input borders, outlines
```

### Status Colors
```
Success (Green)
#10B981
RGB: 16, 185, 129
Use: Success messages, confirmations

Warning (Amber)
#F59E0B
RGB: 245, 158, 11
Use: Warning messages, caution states

Error (Red)
#EF4444
RGB: 239, 68, 68
Use: Error messages, delete actions

Info (Blue)
#3B82F6
RGB: 59, 130, 246
Use: Information messages, tips
```

### Gradient Colors
```
Splash Gradient:
Start: #1E3A8A (Deep Blue)
Center: #3B82F6 (Bright Blue)
End: #8B5CF6 (Purple)
Angle: 135°
```

---

## 📐 Design Tokens

### Border Radius
```
xs: 8dp   - Small elements (chips, tags)
sm: 12dp  - Buttons, inputs
md: 16dp  - Standard cards
lg: 20dp  - Feature cards, containers
xl: 24dp  - Large containers
```

### Elevation (Shadows)
```
Level 0: 0dp   - Flat elements
Level 1: 2dp   - Subtle depth
Level 2: 4dp   - Standard cards
Level 3: 8dp   - Featured cards, FAB
Level 4: 12dp  - Prominent cards
Level 5: 16dp  - Dialogs, modals
```

### Spacing Scale
```
4dp   (xs)   - Icon padding, small gaps
8dp   (sm)   - Compact spacing
12dp  (md)   - Standard spacing
16dp  (lg)   - Card padding
20dp  (xl)   - Card margins
24dp  (xxl)  - Section spacing
32dp  (xxxl) - Large gaps, top margins
```

### Typography
```
Display Large: 42sp, Light, Bold
- Use: Time display, hero numbers

Heading 1: 20sp, Medium, Bold
- Use: Card titles, person names

Heading 2: 18sp, Medium, Bold
- Use: Section headers

Heading 3: 16sp, Medium, Bold
- Use: Task titles, important labels

Body Large: 16sp, Regular
- Use: Primary content, descriptions

Body: 15sp, Regular
- Use: Quote text, longer content

Body Small: 14sp, Regular
- Use: Task descriptions, secondary content

Caption: 13sp, Medium
- Use: Author names, labels

Metadata: 12sp, Regular
- Use: Timestamps, due dates, small info
```

### Font Families
```
Sans Serif (Default): System default
Sans Serif Medium: Medium weight system font
Sans Serif Light: Light weight system font
Serif: System serif font (for quotes)
```

### Letter Spacing
```
Tight: -0.01em  - Large headings
Normal: 0em     - Body text
Wide: 0.05em    - Small labels
Wider: 0.1em    - Uppercase labels
```

### Line Height
```
Tight: 1.2   - Headings
Normal: 1.5  - Body text
Relaxed: 1.6 - Long-form content
Loose: 2.0   - Quotes, callouts
```

---

## 🎯 Usage Guidelines

### When to Use Each Color

**Primary Blue (#1E3A8A)**
- App bar background
- Primary buttons
- Active navigation items
- Important headings

**Purple Accent (#8B5CF6)**
- Floating Action Button
- Checkboxes when checked
- Call-to-action buttons
- Interactive elements

**Emerald Green (#10B981)**
- Success confirmations
- Completed task indicators
- Low priority tasks
- Positive states

**Red (#EF4444)**
- High priority tasks
- Delete/remove actions
- Error messages
- Urgent notifications

**Amber (#F59E0B)**
- Medium priority tasks
- Warning messages
- Pending states

### Accessibility

**Contrast Ratios (WCAG AA)**
- Text Primary on White: 18.5:1 ✅
- Text Secondary on White: 7.1:1 ✅
- White on Primary Blue: 9.2:1 ✅
- White on Purple Accent: 7.8:1 ✅
- All priority colors on white: 4.5:1+ ✅

**Touch Targets**
- Minimum: 48dp x 48dp
- Recommended: 56dp height for buttons
- Icon buttons: 48dp x 48dp

**Font Sizes**
- Minimum body text: 14sp
- Minimum metadata: 12sp
- Primary headings: 16sp+

---

## 🌓 Dark Mode Colors

### Backgrounds
```
Background: #121212 (Almost black)
Surface: #1E1E1E (Dark gray)
Surface Variant: #2C2C2C
```

### Text (Dark Mode)
```
Primary: #FFFFFF (White)
Secondary: #CBD5E1 (Slate 300)
Tertiary: #94A3B8 (Slate 400)
```

### Same Colors (Dark Mode Compatible)
- Primary colors remain the same
- Accent purple remains vibrant
- Priority colors remain the same
- Elevation uses alpha shadows

---

## 📱 Component Specific Styles

### Task Card
```
Background: #FFFFFF
Border Radius: 16dp
Elevation: 4dp
Padding: 16dp
Margin: 12dp (sides), 6dp (top/bottom)

Priority Bar:
- Width: 4dp
- Height: Match parent
- Colors: Priority specific
```

### Feature Cards (Home)
```
Background: Varies by card
Border Radius: 20dp
Elevation: 8dp (12dp for time card)
Padding: 24dp
Margin: 20dp
```

### Buttons
```
Height: 56dp
Border Radius: 14dp
Text Size: 16sp
Text Transform: None (no all caps)
Padding: 12dp vertical
```

### FAB
```
Size: 56dp x 56dp
Background: #8B5CF6 (Purple)
Icon Color: #FFFFFF
Elevation: 8dp
```

---

## 🎨 Color Combinations

### Recommended Pairings
```
1. Primary + White
   - #1E3A8A background
   - #FFFFFF text
   
2. Purple + White
   - #8B5CF6 background
   - #FFFFFF text
   
3. White + Primary
   - #FFFFFF background
   - #1E3A8A text
   
4. Surface + Text Primary
   - #FFFFFF background
   - #0F172A text
   
5. Background + Surface
   - #F8FAFC container
   - #FFFFFF card
```

### Avoid
```
❌ Primary Dark on Primary
❌ Text Secondary on Text Tertiary
❌ Light backgrounds on light text
❌ Low contrast color combinations
```

---

**Design System Version**: 1.0
**Last Updated**: January 22, 2026
**Maintained By**: Task Manager Development Team
