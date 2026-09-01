with open("app/src/main/java/com/example/lexicon/ui/screens/SettingsScreen.kt", "r") as f:
    content = f.read()

# I want to find the exact place to replace.
target = """            }
Text("AI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
            // AI / API Section"""

replacement = """            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("AI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
            // AI / API Section"""

content = content.replace(target, replacement)

# if the first try didn't work because of indentation or something, let's use regex
import re
content = re.sub(r'(\s*})\s*Text\("AI", style = MaterialTheme\.typography\.labelMedium, color = MaterialTheme\.colorScheme\.primary, modifier = Modifier\.padding\(start = 16\.dp, bottom = 8\.dp\)\)\s*// AI / API Section', 
                 r'\1\n            \n            Spacer(modifier = Modifier.height(16.dp))\n            \n            Text("AI", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))\n            // AI / API Section', 
                 content)

with open("app/src/main/java/com/example/lexicon/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(content)

print("SettingsScreen updated successfully")
