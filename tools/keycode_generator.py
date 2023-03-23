import json
import re
from typing import Dict, List

if __name__ == "__main__":
    with open("./keycodes.json", "r", errors="ignore") as f:
        content = f.read()
        keycodes: List[Dict[str, str]] = json.loads(content)
        lines = []
        regex = re.compile(r"(?!^)([A-Z]+)")
        for entry in keycodes:
            code = entry["event.code"]
            if code == "":
                continue
            name = regex.sub(r"_\1", code).upper()

            if name[0].isnumeric():
                name = f"NUMBER_{name}"
            # description = "".join(map(lambda x: x if x.isalnum() else "_", description))
            lines.append(
                f"    const val {name} = \"{code}\""
            )
        class_content = "\n".join(lines)
        result = f"object KeyCodes {{\n{class_content}\n}}"
        f = open("./KeyCodes.kt", "w")
        f.write(result)
