from __future__ import annotations

import importlib.util
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
AUDITOR = ROOT / "tools/content-validator/audit_pedagogical_semantics.py"


class FullContentPedagogyAuditTest(unittest.TestCase):

    def test_all_authored_activities_and_runtime_semantics(self) -> None:
        spec = importlib.util.spec_from_file_location("audit_pedagogy", AUDITOR)
        assert spec and spec.loader
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        errors, report = module.audit()
        self.assertEqual(125, report["activities"])
        self.assertFalse(errors, msg="\n" + "\n".join(errors))


if __name__ == "__main__":
    unittest.main()
