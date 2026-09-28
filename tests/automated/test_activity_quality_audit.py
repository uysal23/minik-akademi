from __future__ import annotations

import subprocess
import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
AUDIT = ROOT / "tools" / "content-validator" / "audit_activity_quality.py"


class ActivityQualityAuditTest(unittest.TestCase):

    def test_full_activity_quality_audit_passes(self) -> None:
        result = subprocess.run(
            [sys.executable, str(AUDIT)],
            cwd=ROOT,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
        )
        self.assertEqual(0, result.returncode, msg=result.stdout)
        self.assertIn("ACTIVITY QUALITY AUDIT PASSED", result.stdout)
        self.assertIn("total activities: 125", result.stdout)
        self.assertIn("safety review: 125/125", result.stdout)


if __name__ == "__main__":
    unittest.main()
