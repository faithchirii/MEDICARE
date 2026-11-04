package org.example.medicare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardController {
    @FXML private AnchorPane dashboardHomePane, patientsPane, doctorsPane, appointmentsPane, medicalRecordsPane, billingPane, pharmacyPane, reportsPane, settingsPane;
    @FXML private Label lblStatPatients, lblStatAppointments, lblStatDoctors;

    @FXML private TableView<PatientModel> patientTable;
    @FXML private TableColumn<PatientModel, Integer> colPatientId;
    @FXML private TableColumn<PatientModel, String> colPatientName, colPatientDob, colPatientGender, colPatientDisease, colPatientContact, colPatientAddress;
    @FXML private TextField searchField, nameField, dobField, genderField, diseaseField, contactField, addressField;

    @FXML private TableView<DoctorModel> doctorTable;
    @FXML private TableColumn<DoctorModel, Integer> colDoctorId;
    @FXML private TableColumn<DoctorModel, String> colDoctorName, colDoctorSpecialization, colDoctorContact;
    @FXML private TextField docNameField, docSpecializationField, docContactField;

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> colApptId;
    @FXML private TableColumn<Appointment, String> colApptPatient, colApptDoctor, colApptDate, colApptTime, colApptReason, colApptStatus;
    @FXML private TextField apptSearchField, apptPatientField, apptDoctorField, apptDateField, apptTimeField, apptReasonField;
    @FXML private ComboBox<String> apptStatusFilter;

    @FXML private TextField medRecordPatientField;

    @FXML private TableView<BillModel> billTable;
    @FXML private TableColumn<BillModel, Integer> colBillId;
    @FXML private TableColumn<BillModel, String> colBillPatient, colBillStatus;
    @FXML private TableColumn<BillModel, Double> colBillTotal, colBillPaid, colBillBalance;
    @FXML private TextField billPatientField, billTotalField, billPaidField;

    @FXML private TableView<MedicineModel> medicineTable;
    @FXML private TableColumn<MedicineModel, Integer> colMedId;
    @FXML private TableColumn<MedicineModel, String> colMedName, colMedExpiry;
    @FXML private TableColumn<MedicineModel, Integer> colMedQty;
    @FXML private TableColumn<MedicineModel, Double> colMedPrice;
    @FXML private TextField medNameField, medQtyField, medPriceField, medExpiryField;

    @FXML private PieChart genderPieChart;

    private final ObservableList<PatientModel> patientList = FXCollections.observableArrayList();
    private final ObservableList<DoctorModel> doctorList = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private final ObservableList<BillModel> billList = FXCollections.observableArrayList();
    private final ObservableList<MedicineModel> medicineList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPatientDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colPatientGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colPatientDisease.setCellValueFactory(new PropertyValueFactory<>("disease"));
        colPatientContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colPatientAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        colDoctorId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDoctorSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colDoctorContact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        colApptId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colApptPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colApptDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colApptDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colApptTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colApptReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colApptStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        if (colBillId != null) {
            colBillId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colBillPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
            colBillTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            colBillPaid.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
            colBillBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
            colBillStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

        if (colMedId != null) {
            colMedId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colMedName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colMedQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            colMedPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            colMedExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        }

        if (apptStatusFilter != null) {
            apptStatusFilter.setItems(FXCollections.observableArrayList("All Statuses", "Scheduled", "Completed"));
            apptStatusFilter.setValue("All Statuses");
        }

        loadPatients();
        loadDoctors();
        loadAppointments();
        loadBills();
        loadMedicines();
        updateDashboardStats();
        setupSearchFilter();
        setupAppointmentFilter();
        loadAnalytics();
    }

    @FXML private void showDashboardView() { switchPane(dashboardHomePane); updateDashboardStats(); }
    @FXML private void showPatientsView() { switchPane(patientsPane); }
    @FXML private void showDoctorsView() { switchPane(doctorsPane); }
    @FXML private void showAppointmentsView() { switchPane(appointmentsPane); }
    @FXML private void showMedicalRecordsView() { switchPane(medicalRecordsPane); }
    @FXML private void showBillingView() { switchPane(billingPane); }
    @FXML private void showPharmacyView() { switchPane(pharmacyPane); }
    @FXML private void showReportsView() { switchPane(reportsPane); loadAnalytics(); }
    @FXML private void showSettingsView() { switchPane(settingsPane); }

    private void switchPane(AnchorPane target) {
        dashboardHomePane.setVisible(false);
        patientsPane.setVisible(false);
        doctorsPane.setVisible(false);
        appointmentsPane.setVisible(false);
        medicalRecordsPane.setVisible(false);
        billingPane.setVisible(false);
        pharmacyPane.setVisible(false);
        reportsPane.setVisible(false);
        settingsPane.setVisible(false);
        target.setVisible(true);
    }

    private void updateDashboardStats() {
        if (lblStatPatients != null) lblStatPatients.setText(String.valueOf(patientList.size()));
        if (lblStatAppointments != null) lblStatAppointments.setText(String.valueOf(appointmentList.size()));
        if (lblStatDoctors != null) lblStatDoctors.setText(String.valueOf(doctorList.size()));
    }

    private void setupSearchFilter() {
        FilteredList<PatientModel> filteredData = new FilteredList<>(patientList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerFilter = newValue.toLowerCase();
                return patient.getName().toLowerCase().contains(lowerFilter) ||
                        patient.getDisease().toLowerCase().contains(lowerFilter) ||
                        patient.getContact().toLowerCase().contains(lowerFilter);
            });
        });
        SortedList<PatientModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(patientTable.comparatorProperty());
        patientTable.setItems(sortedData);
    }

    private void setupAppointmentFilter() {
        FilteredList<Appointment> filteredAppts = new FilteredList<>(appointmentList, a -> true);

        Runnable updatePredicate = () -> {
            String searchText = apptSearchField.getText() == null ? "" : apptSearchField.getText().toLowerCase().trim();
            String selectedStatus = apptStatusFilter.getValue() == null ? "All Statuses" : apptStatusFilter.getValue();

            filteredAppts.setPredicate(appt -> {
                boolean matchesSearch = searchText.isEmpty() ||
                        appt.getPatientName().toLowerCase().contains(searchText) ||
                        appt.getDoctorName().toLowerCase().contains(searchText);

                boolean matchesStatus = selectedStatus.equalsIgnoreCase("All Statuses") ||
                        appt.getStatus().equalsIgnoreCase(selectedStatus);

                return matchesSearch && matchesStatus;
            });
        };

        if (apptSearchField != null) apptSearchField.textProperty().addListener((obs, oldV, newV) -> updatePredicate.run());
        if (apptStatusFilter != null) apptStatusFilter.valueProperty().addListener((obs, oldV, newV) -> updatePredicate.run());

        SortedList<Appointment> sortedAppts = new SortedList<>(filteredAppts);
        sortedAppts.comparatorProperty().bind(appointmentTable.comparatorProperty());
        appointmentTable.setItems(sortedAppts);
    }

    private void loadPatients() {
        patientList.clear();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM patients")) {
            while (rs.next()) {
                patientList.add(new PatientModel(
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("dob"), rs.getString("gender"),
                        rs.getString("disease"), rs.getString("contact"),
                        rs.getString("address")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadDoctors() {
        doctorList.clear();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM doctors")) {
            while (rs.next()) {
                doctorList.add(new DoctorModel(
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("specialization"), rs.getString("contact")
                ));
            }
            if (doctorTable != null) doctorTable.setItems(doctorList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadAppointments() {
        appointmentList.clear();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM appointments")) {
            while (rs.next()) {
                appointmentList.add(new Appointment(
                        rs.getInt("id"), rs.getString("patient_name"),
                        rs.getString("doctor_name"), rs.getString("date"),
                        rs.getString("time"), rs.getString("reason"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadBills() {
        billList.clear();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bills")) {
            while (rs.next()) {
                billList.add(new BillModel(
                        rs.getInt("id"), rs.getString("patient_name"),
                        rs.getDouble("total_amount"), rs.getDouble("amount_paid"),
                        rs.getDouble("balance"), rs.getString("status")
                ));
            }
            if (billTable != null) billTable.setItems(billList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadMedicines() {
        medicineList.clear();
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM medicines")) {
            while (rs.next()) {
                medicineList.add(new MedicineModel(
                        rs.getInt("id"), rs.getString("name"),
                        rs.getInt("quantity"), rs.getDouble("price"),
                        rs.getString("expiry_date")
                ));
            }
            if (medicineTable != null) medicineTable.setItems(medicineList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void loadAnalytics() {
        int maleCount = 0, femaleCount = 0, otherCount = 0;
        for (PatientModel p : patientList) {
            String g = p.getGender().trim();
            if (g.equalsIgnoreCase("Male") || g.equalsIgnoreCase("M")) maleCount++;
            else if (g.equalsIgnoreCase("Female") || g.equalsIgnoreCase("F")) femaleCount++;
            else otherCount++;
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Male (" + maleCount + ")", maleCount),
                new PieChart.Data("Female (" + femaleCount + ")", femaleCount),
                new PieChart.Data("Other (" + otherCount + ")", otherCount)
        );
        if (genderPieChart != null) genderPieChart.setData(pieChartData);
    }

    @FXML
    private void handleAddPatient() {
        try {
            String query = "INSERT INTO patients (name, dob, gender, disease, contact, address) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, dobField.getText().trim());
                pstmt.setString(3, genderField.getText().trim());
                pstmt.setString(4, diseaseField.getText().trim());
                pstmt.setString(5, contactField.getText().trim());
                pstmt.setString(6, addressField.getText().trim());
                pstmt.executeUpdate();
            }
            loadPatients();
            updateDashboardStats();
            nameField.clear(); dobField.clear(); genderField.clear(); diseaseField.clear(); contactField.clear(); addressField.clear();
            showAlert("Success", "Patient registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to register patient: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddDoctor() {
        try {
            String query = "INSERT INTO doctors (name, specialization, contact) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, docNameField.getText().trim());
                pstmt.setString(2, docSpecializationField.getText().trim());
                pstmt.setString(3, docContactField.getText().trim());
                pstmt.executeUpdate();
            }
            loadDoctors();
            updateDashboardStats();
            docNameField.clear(); docSpecializationField.clear(); docContactField.clear();
            showAlert("Success", "Doctor registered successfully!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleBookAppointment() {
        try {
            String query = "INSERT INTO appointments (patient_name, doctor_name, date, time, reason, status) VALUES (?, ?, ?, ?, ?, 'Scheduled')";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, apptPatientField.getText().trim());
                pstmt.setString(2, apptDoctorField.getText().trim());
                pstmt.setString(3, apptDateField.getText().trim());
                pstmt.setString(4, apptTimeField.getText().trim());
                pstmt.setString(5, apptReasonField.getText().trim());
                pstmt.executeUpdate();
            }
            loadAppointments();
            updateDashboardStats();
            apptPatientField.clear(); apptDoctorField.clear(); apptDateField.clear(); apptTimeField.clear(); apptReasonField.clear();
            showAlert("Success", "Appointment booked successfully!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleAddMedicine() {
        try {
            String query = "INSERT INTO medicines (name, quantity, price, expiry_date) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, medNameField.getText().trim());
                pstmt.setInt(2, Integer.parseInt(medQtyField.getText().trim()));
                pstmt.setDouble(3, Double.parseDouble(medPriceField.getText().trim()));
                pstmt.setString(4, medExpiryField.getText().trim());
                pstmt.executeUpdate();
            }
            loadMedicines();
            medNameField.clear(); medQtyField.clear(); medPriceField.clear(); medExpiryField.clear();
            showAlert("Success", "Medicine added to pharmacy inventory!");
        } catch (Exception e) {
            showAlert("Error", "Please enter valid inventory details.");
        }
    }

    @FXML
    private void handleGenerateBill() {
        try {
            double total = Double.parseDouble(billTotalField.getText().trim());
            double paid = Double.parseDouble(billPaidField.getText().trim());
            double balance = total - paid;
            String status = balance <= 0 ? "Paid" : "Pending";

            String query = "INSERT INTO bills (patient_name, total_amount, amount_paid, balance, status) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, billPatientField.getText().trim());
                pstmt.setDouble(2, total);
                pstmt.setDouble(3, paid);
                pstmt.setDouble(4, balance);
                pstmt.setString(5, status);
                pstmt.executeUpdate();
            }
            loadBills();
            billPatientField.clear(); billTotalField.clear(); billPaidField.clear();
            showAlert("Success", "Bill generated! Balance: KSh " + balance);
        } catch (Exception e) {
            showAlert("Invalid Input", "Please enter valid numeric amounts.");
        }
    }

    @FXML
    private void handleMarkBillPaid() {
        BillModel selected = billTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select a bill."); return; }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE bills SET amount_paid = total_amount, balance = 0, status = 'Paid' WHERE id = ?")) {
            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadBills();
            showAlert("Success", "Bill marked as Paid!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handlePrintReceipt() {
        BillModel selected = billTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select a bill to view receipt."); return; }

        Stage receiptStage = new Stage();
        receiptStage.setTitle("Official Receipt - Bill #" + selected.getId());
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        String receiptText = String.format(
                "===================================\n" +
                        "         MEDICARE HOSPITAL         \n" +
                        "          OFFICIAL RECEIPT         \n" +
                        "===================================\n" +
                        "Bill ID: #%d\n" +
                        "Patient: %s\n\n" +
                        "Total Amount:     KSh %.2f\n" +
                        "Amount Paid:      KSh %.2f\n" +
                        "Outstanding Bal:  KSh %.2f\n" +
                        "Payment Status:   %s\n" +
                        "===================================",
                selected.getId(), selected.getPatientName(),
                selected.getTotalAmount(), selected.getAmountPaid(),
                selected.getBalance(), selected.getStatus()
        );

        TextArea area = new TextArea(receiptText);
        area.setEditable(false);
        area.setPrefHeight(250);

        layout.getChildren().addAll(new Label("Payment Receipt:"), area);
        receiptStage.setScene(new Scene(layout, 350, 320));
        receiptStage.show();
    }

    @FXML
    private void handleMarkAppointmentCompleted() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select an appointment."); return; }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE appointments SET status = 'Completed' WHERE id = ?")) {
            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadAppointments();
            showAlert("Success", "Appointment marked as Completed!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleDeletePatient() {
        PatientModel selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select a patient."); return; }
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM patients WHERE id = ?")) {
            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadPatients();
            updateDashboardStats();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleDeleteDoctor() {
        DoctorModel selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select a doctor."); return; }
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM doctors WHERE id = ?")) {
            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadDoctors();
            updateDashboardStats();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleDeleteAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select an appointment."); return; }
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM appointments WHERE id = ?")) {
            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadAppointments();
            updateDashboardStats();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleDeleteMedicine() {
        MedicineModel selected = medicineTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select a medicine."); return; }
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM medicines WHERE id = ?")) {
            pstmt.setInt(1, selected.getId());
            pstmt.executeUpdate();
            loadMedicines();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleOpenMedicalHistory() {
        String patName = medRecordPatientField.getText().trim();
        if (patName.isEmpty()) { showAlert("Input Error", "Please enter a patient name."); return; }

        Stage historyStage = new Stage();
        historyStage.setTitle("Medical History - " + patName);
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 15;");

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Enter diagnosis / prescription notes...");
        Button saveNoteBtn = new Button("Add Record Note");

        ListView<String> historyList = new ListView<>();
        ObservableList<String> notesData = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT notes, date FROM medical_history m JOIN patients p ON m.patient_id = p.id WHERE p.name LIKE ?")) {
            pstmt.setString(1, "%" + patName + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                notesData.add("[" + rs.getString("date") + "] " + rs.getString("notes"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        historyList.setItems(notesData);

        layout.getChildren().addAll(new Label("Consultation Records for " + patName + ":"), historyList, notesArea, saveNoteBtn);
        historyStage.setScene(new Scene(layout, 400, 400));
        historyStage.show();
    }

    @FXML
    private void handleExportPatientsCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Patients CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(patientTable.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("ID,Name,DOB,Gender,Disease,Contact,Address\n");
                for (PatientModel p : patientList) {
                    writer.write(p.getId() + "," + p.getName() + "," + p.getDob() + "," + p.getGender() + "," + p.getDisease() + "," + p.getContact() + "," + p.getAddress() + "\n");
                }
                showAlert("Success", "Patients exported to CSV successfully!");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/medicare/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) patientTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Medicare System - Login");
            stage.centerOnScreen();
            stage.setMaximized(false);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleMinimize() { ((Stage) patientTable.getScene().getWindow()).setIconified(true); }
    @FXML private void handleClose() { ((Stage) patientTable.getScene().getWindow()).close(); }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}