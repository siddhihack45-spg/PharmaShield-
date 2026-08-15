package com.example.pharmashield

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Section 1: Verification Views
    private lateinit var etSerialInput: EditText
    private lateinit var btnVerifySerial: Button
    private lateinit var btnCameraScan: Button
    private lateinit var tvVerificationResult: TextView
    private lateinit var btnEmergencySos: Button

    // Section 2: Pill Matcher Views
    private lateinit var etPillImprint: EditText
    private lateinit var btnFilterPills: Button
    private lateinit var tvPillResults: TextView

    // Section 3: Interaction Matrix Views
    private lateinit var cbWarfarin: CheckBox
    private lateinit var cbAspirin: CheckBox
    private lateinit var cbIbuprofen: CheckBox
    private lateinit var cbMetformin: CheckBox
    private lateinit var cbKidney: CheckBox
    private lateinit var cbPregnancy: CheckBox
    private lateinit var btnEvaluateMatrix: Button
    private lateinit var tvInteractionOutput: TextView

    // Section 4: Schedule Views
    private lateinit var tvAdherenceScore: TextView
    private lateinit var dose1: CheckBox
    private lateinit var dose2: CheckBox
    private lateinit var dose3: CheckBox
    private lateinit var dose4: CheckBox
    private lateinit var btnAutoRefill: Button

    // Section 5: Chemist
    private lateinit var btnCallPharmacy: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()

        // Display initial preset verification
        displayVerification("AUTHENTIC-AZ-9942")
        evaluatePills("")
        evaluateInteractions()
        updateAdherenceScore()
    }

    private fun initViews() {
        btnEmergencySos = findViewById(R.id.btnEmergencySos)
        btnCameraScan = findViewById(R.id.btnCameraScan)
        etSerialInput = findViewById(R.id.etSerialInput)
        btnVerifySerial = findViewById(R.id.btnVerifySerial)
        tvVerificationResult = findViewById(R.id.tvVerificationResult)

        etPillImprint = findViewById(R.id.etPillImprint)
        btnFilterPills = findViewById(R.id.btnFilterPills)
        tvPillResults = findViewById(R.id.tvPillResults)

        cbWarfarin = findViewById(R.id.cbWarfarin)
        cbAspirin = findViewById(R.id.cbAspirin)
        cbIbuprofen = findViewById(R.id.cbIbuprofen)
        cbMetformin = findViewById(R.id.cbMetformin)
        cbKidney = findViewById(R.id.cbKidney)
        cbPregnancy = findViewById(R.id.cbPregnancy)
        btnEvaluateMatrix = findViewById(R.id.btnEvaluateMatrix)
        tvInteractionOutput = findViewById(R.id.tvInteractionOutput)

        tvAdherenceScore = findViewById(R.id.tvAdherenceScore)
        dose1 = findViewById(R.id.dose1)
        dose2 = findViewById(R.id.dose2)
        dose3 = findViewById(R.id.dose3)
        dose4 = findViewById(R.id.dose4)
        btnAutoRefill = findViewById(R.id.btnAutoRefill)

        btnCallPharmacy = findViewById(R.id.btnCallPharmacy)

        // Preset Chips
        findViewById<Button>(R.id.chipAuthentic1)?.setOnClickListener { displayVerification("AUTHENTIC-AZ-9942") }
        findViewById<Button>(R.id.chipAuthentic2)?.setOnClickListener { displayVerification("AUTHENTIC-AM-7721") }
        findViewById<Button>(R.id.chipFake)?.setOnClickListener { displayVerification("FAKE-COUNTERFEIT-108") }
        findViewById<Button>(R.id.chipRecalled)?.setOnClickListener { displayVerification("RECALLED-BATCH-404") }
    }

    private fun setupListeners() {
        btnVerifySerial.setOnClickListener {
            val code = etSerialInput.text.toString().trim()
            if (code.isNotEmpty()) displayVerification(code)
            else Toast.makeText(this, "Enter a valid Serial / GTIN Number", Toast.LENGTH_SHORT).show()
        }

        btnCameraScan.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SCANNER)
        }

        btnEmergencySos.setOnClickListener {
            showEmergencySosDialog()
        }

        btnFilterPills.setOnClickListener {
            evaluatePills(etPillImprint.text.toString())
        }

        btnEvaluateMatrix.setOnClickListener {
            evaluateInteractions()
        }

        val doseListener = android.view.View.OnClickListener { updateAdherenceScore() }
        dose1.setOnClickListener(doseListener)
        dose2.setOnClickListener(doseListener)
        dose3.setOnClickListener(doseListener)
        dose4.setOnClickListener(doseListener)

        btnAutoRefill.setOnClickListener {
            Toast.makeText(this, "Auto-Refill request dispatched to verified pharmacy node!", Toast.LENGTH_LONG).show()
        }

        btnCallPharmacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18005550199"))
            startActivity(intent)
        }
    }

    private fun displayVerification(code: String) {
        etSerialInput.setText(code)
        val result = VerificationEngine.verifyCode(code)

        val sb = StringBuilder()
        sb.append("=== ").append(result.status).append(" MEDICINE VERIFICATION ===\n")
        sb.append("Safety Score: ").append(result.safetyScore).append("%\n\n")
        sb.append("📌 Drug Name: ").append(result.drugName).append("\n")
        sb.append("⚖️ Strength: ").append(result.dosage).append("\n")
        sb.append("🏭 Manufacturer: ").append(result.manufacturer).append("\n")
        sb.append("🏷️ Batch Lot #: ").append(result.batchNo).append("\n")
        sb.append("📊 GTIN Barcode: ").append(result.gtin).append("\n")
        sb.append("📅 Mfg Date: ").append(result.mfd).append(" | Exp Date: ").append(result.exp).append("\n\n")
        sb.append("🔒 Lab Checksum: ").append(result.labChecksum).append("\n\n")

        sb.append("--- SUPPLY CHAIN PROVENANCE LEDGER ---\n")
        result.provenance.forEachIndexed { index, step ->
            val icon = if (step.isCompleted) "✅" else "❌"
            sb.append(icon).append(" Step ").append(index + 1).append(": ").append(step.stepName).append("\n")
            sb.append("    Location: ").append(step.location).append(" (").append(step.timestamp).append(")\n")
        }
        sb.append("\n⚠️ Warning: ").append(result.warnings)

        tvVerificationResult.text = sb.toString()
    }

    private fun evaluatePills(query: String) {
        val matches = PillMatcher.filterPills("all", "all", query)
        if (matches.isEmpty()) {
            tvPillResults.text = "No matching pills found for query."
            return
        }
        val sb = StringBuilder()
        matches.forEach { pill ->
            sb.append("💊 ").append(pill.name).append(" (").append(pill.dosage).append(")\n")
            sb.append("   Imprint Code: ").append(pill.imprint).append(" | Shape: ").append(pill.shape).append(" | Color: ").append(pill.color).append("\n")
            sb.append("   Category: ").append(pill.category).append(" | Mfg: ").append(pill.manufacturer).append("\n\n")
        }
        tvPillResults.text = sb.toString().trim()
    }

    private fun evaluateInteractions() {
        val selectedMeds = mutableListOf<String>()
        if (cbWarfarin.isChecked) selectedMeds.add("Warfarin")
        if (cbAspirin.isChecked) selectedMeds.add("Aspirin")
        if (cbIbuprofen.isChecked) selectedMeds.add("Ibuprofen")
        if (cbMetformin.isChecked) selectedMeds.add("Metformin")

        val conditions = mutableListOf<String>()
        if (cbKidney.isChecked) conditions.add("Kidney Impairment")
        if (cbPregnancy.isChecked) conditions.add("Pregnancy")

        val hazards = InteractionMatrix.evaluate(selectedMeds, conditions)
        if (hazards.isEmpty()) {
            tvInteractionOutput.text = "✓ No high-risk cross interactions detected for selected medications."
            return
        }

        val sb = StringBuilder()
        hazards.forEach { hazard ->
            sb.append("[").append(hazard.riskLevel).append(" HAZARD] ").append(hazard.title).append("\n")
            sb.append("Biochemical Mechanism: ").append(hazard.mechanism).append("\n")
            sb.append("Action: ").append(hazard.recommendation).append("\n\n")
        }
        tvInteractionOutput.text = sb.toString().trim()
    }

    private fun updateAdherenceScore() {
        val count = listOf(dose1.isChecked, dose2.isChecked, dose3.isChecked, dose4.isChecked).count { it }
        val percent = ((count.toDouble() / 4) * 100).toInt()
        tvAdherenceScore.text = "Today's Adherence Score: $percent% ($count of 4 doses taken)"
    }

    private fun showEmergencySosDialog() {
        AlertDialog.Builder(this)
            .setTitle("🚨 EMERGENCY POISON CONTROL & SOS")
            .setMessage("If you suspect accidental double-dosing, adverse toxicity, or consumption of a counterfeit drug, take immediate action:")
            .setPositiveButton("Call Poison Control (1-800-222-1222)") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18002221222"))
                startActivity(intent)
            }
            .setNegativeButton("Call EMS (911)") { _, _ ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
                startActivity(intent)
            }
            .setNeutralButton("Close", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCANNER && resultCode == RESULT_OK) {
            val scannedText = data?.getStringExtra("SCANNED_TEXT") ?: ""
            if (scannedText.isNotBlank()) {
                Toast.makeText(this, "OCR Scan Captured: $scannedText", Toast.LENGTH_LONG).show()
                displayVerification(scannedText)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SCANNER = 1001
    }
}
