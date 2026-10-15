package com.carcrowd.config;

import com.carcrowd.entity.Car;
import com.carcrowd.entity.CarBrand;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.repository.CarBrandRepository;
import com.carcrowd.repository.CarRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.carcrowd.entity.AdminUser;
import com.carcrowd.entity.LeadActivity;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.repository.AdminUserRepository;
import com.carcrowd.repository.LeadActivityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CarBrandRepository brandRepository;
    private final CarRepository carRepository;
    private final InterestRegistrationRepository interestRepository;
    private final AdminUserRepository adminUserRepository;
    private final LeadActivityRepository activityRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.admin.initial-email:admin@carcrowd.in}")
    private String initialAdminEmail;

    @org.springframework.beans.factory.annotation.Value("${app.admin.initial-password:Admin@CarCrowd2025}")
    private String initialAdminPassword;

    @org.springframework.beans.factory.annotation.Value("${app.admin.initial-name:CarCrowd Operations Admin}")
    private String initialAdminName;

    public DataSeeder(CarBrandRepository brandRepository,
                      CarRepository carRepository,
                      InterestRegistrationRepository interestRepository,
                      AdminUserRepository adminUserRepository,
                      LeadActivityRepository activityRepository,
                      PasswordEncoder passwordEncoder) {
        this.brandRepository = brandRepository;
        this.carRepository = carRepository;
        this.interestRepository = interestRepository;
        this.adminUserRepository = adminUserRepository;
        this.activityRepository = activityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing CarCrowd database catalog and checking administrative account provisioning...");

        // Remove previous catalog and activity records for clean dev seed if catalog is empty or being re-seeded
        activityRepository.deleteAll();
        interestRepository.deleteAll();
        carRepository.deleteAll();
        brandRepository.deleteAll();

        // Secure admin provisioning: do not wipe admin accounts; create initial admin only if not already present
        if (adminUserRepository.findByEmailIgnoreCase(initialAdminEmail).isEmpty()) {
            adminUserRepository.save(new AdminUser(
                initialAdminEmail,
                passwordEncoder.encode(initialAdminPassword),
                initialAdminName,
                "ROLE_ADMIN",
                true
            ));
            log.info("Initial administrator account provisioned securely for email: {}", initialAdminEmail);
        } else {
            log.info("Administrator account already verified for email: {}", initialAdminEmail);
        }

        // 1. Maruti Suzuki
        CarBrand maruti = seedBrand("Maruti Suzuki");
        seedCar(maruti, "Alto K10", "Hatchback", "Petrol / S-CNG", "Manual / AGS", "5 Seater", 4);
        seedCar(maruti, "S-Presso", "Mini SUV / Hatchback", "Petrol / S-CNG", "Manual / AGS", "5 Seater", 3);
        seedCar(maruti, "Wagon R", "Tallboy Hatchback", "Petrol / S-CNG", "Manual / AGS", "5 Seater", 6);
        seedCar(maruti, "Celerio", "Hatchback", "Petrol / S-CNG", "Manual / AGS", "5 Seater", 2);
        seedCar(maruti, "Swift", "Hatchback", "1.2L Z-Series Petrol", "Manual / AGS", "5 Seater", 11);
        seedCar(maruti, "Baleno", "Premium Hatchback", "Petrol / S-CNG", "Manual / AGS", "5 Seater", 8);
        seedCar(maruti, "Dzire", "Compact Sedan", "Petrol / S-CNG", "Manual / AGS", "5 Seater", 7);
        seedCar(maruti, "Fronx", "Crossover Coupe", "1.2L DualJet / 1.0L Turbo Boosterjet", "Manual / 6-AT", "5 Seater", 9);
        seedCar(maruti, "Brezza", "Compact SUV", "1.5L K-Series Petrol / S-CNG", "Manual / 6-AT", "5 Seater", 12);
        seedCar(maruti, "Ertiga", "MPV", "1.5L Petrol / S-CNG", "Manual / 6-AT", "7 Seater", 14);
        seedCar(maruti, "Grand Vitara", "Mid-size SUV", "Intelligent Electric Hybrid / Mild Hybrid", "e-CVT / Manual / 6-AT", "5 Seater", 10);
        seedCar(maruti, "XL6", "Premium MPV", "1.5L K-Series Petrol / CNG", "Manual / 6-AT", "6 Seater", 5);
        seedCar(maruti, "Jimny", "Off-road 4x4", "1.5L K15B ALLGRIP PRO", "Manual / 4-AT", "4 Seater", 6);
        seedCar(maruti, "Invicto", "Premium MPV", "2.0L Strong Hybrid", "e-CVT", "7/8 Seater", 4);
        seedCar(maruti, "e Vitara", "Electric SUV", "All-Electric (49 / 61 kWh)", "Automatic", "5 Seater", 8);

        // 2. Tata
        CarBrand tata = seedBrand("Tata");
        seedCar(tata, "Tiago", "Hatchback", "1.2L Revotron Petrol / iCNG", "Manual / AMT", "5 Seater", 5);
        seedCar(tata, "Tigor", "Compact Sedan", "1.2L Revotron Petrol / iCNG", "Manual / AMT", "5 Seater", 3);
        seedCar(tata, "Altroz", "Premium Hatchback", "Petrol / Diesel / iCNG", "Manual / DCA", "5 Seater", 7);
        seedCar(tata, "Punch", "Micro SUV", "1.2L Revotron Petrol / iCNG", "Manual / AMT", "5 Seater", 12);
        seedCar(tata, "Nexon", "Compact SUV", "1.2L Turbo Petrol / 1.5L Diesel", "Manual / DCA / AMT", "5 Seater", 18);
        seedCar(tata, "Curvv", "SUV Coupe", "1.2L Hyperion Turbo / 1.5L Diesel / EV", "Manual / DCA", "5 Seater", 11);
        seedCar(tata, "Harrier", "Mid-size SUV", "2.0L Kryotec Diesel", "Manual / 6-Speed AT", "5 Seater", 8);
        seedCar(tata, "Safari", "Flagship SUV", "2.0L Kryotec Diesel", "Manual / 6-Speed AT", "6/7 Seater", 9);
        seedCar(tata, "Punch EV", "Electric Micro SUV", "All-Electric (Gen 2 acti.ev)", "Automatic", "5 Seater", 7);
        seedCar(tata, "Nexon EV", "Electric Compact SUV", "All-Electric Long Range", "Automatic", "5 Seater", 14);
        seedCar(tata, "Tiago EV", "Electric Hatchback", "All-Electric", "Automatic", "5 Seater", 6);
        seedCar(tata, "Sierra", "Lifestyle SUV", "Turbo Petrol / All-Electric", "Automatic", "5 Seater", 10);

        // 3. Hyundai
        CarBrand hyundai = seedBrand("Hyundai");
        seedCar(hyundai, "Grand i10 Nios", "Hatchback", "1.2L Kappa Petrol / CNG", "Manual / AMT", "5 Seater", 5);
        seedCar(hyundai, "i20", "Premium Hatchback", "1.2L Kappa Petrol", "Manual / IVT", "5 Seater", 8);
        seedCar(hyundai, "Aura", "Compact Sedan", "1.2L Kappa Petrol / CNG", "Manual / AMT", "5 Seater", 4);
        seedCar(hyundai, "Exter", "Micro SUV", "1.2L Bi-Fuel Kappa Petrol / CNG", "Manual / Smart Auto AMT", "5 Seater", 9);
        seedCar(hyundai, "Venue", "Compact SUV", "1.2L Petrol / 1.0L Turbo / 1.5L Diesel", "Manual / 7-DCT", "5 Seater", 11);
        seedCar(hyundai, "Creta", "Mid-size SUV", "1.5L Petrol / 1.5L Turbo / 1.5L Diesel", "Manual / IVT / 7-DCT / 6-AT", "5 Seater", 21);
        seedCar(hyundai, "Alcazar", "6/7-Seater SUV", "1.5L Turbo Petrol / 1.5L Diesel", "Manual / 7-DCT / 6-AT", "6/7 Seater", 7);
        seedCar(hyundai, "Verna", "Executive Sedan", "1.5L MPi / 1.5L Turbo GDi", "Manual / IVT / 7-DCT", "5 Seater", 9);
        seedCar(hyundai, "Tucson", "Flagship SUV", "2.0L Petrol / 2.0L Diesel HTRAC AWD", "6-AT / 8-AT", "5 Seater", 5);
        seedCar(hyundai, "Ioniq 5", "Electric Crossover", "All-Electric (72.6 kWh E-GMP)", "Automatic", "5 Seater", 6);

        // 4. Mahindra
        CarBrand mahindra = seedBrand("Mahindra");
        seedCar(mahindra, "XUV 3XO", "Compact SUV", "1.2L mStallion Turbo / 1.5L mHawk Diesel", "Manual / 6-Speed TC", "5 Seater", 16);
        seedCar(mahindra, "Bolero", "Utility Vehicle", "1.5L mHawk75 Diesel", "5-Speed Manual", "7 Seater", 8);
        seedCar(mahindra, "Bolero Neo", "Compact Rugged SUV", "1.5L mHawk100 Diesel", "5-Speed Manual", "7 Seater", 6);
        seedCar(mahindra, "Thar", "Off-road 4x4", "2.0L mStallion Petrol / 2.2L mHawk Diesel", "Manual / Automatic", "4 Seater", 14);
        seedCar(mahindra, "Thar ROXX", "5-Door Lifestyle SUV", "2.0L Turbo Petrol / 2.2L Gen-2 mHawk Diesel", "Manual / 6-Speed Automatic", "5 Seater", 19);
        seedCar(mahindra, "Scorpio Classic", "Rugged SUV", "2.2L mHawk Diesel", "6-Speed Manual", "7/9 Seater", 10);
        seedCar(mahindra, "Scorpio N", "D-Segment SUV", "2.0L Petrol / 2.2L mHawk 4XPLOR Diesel", "Manual / 6-Speed AT", "6/7 Seater", 17);
        seedCar(mahindra, "XUV700", "Premium Mid SUV", "2.0L Turbo Petrol / 2.2L mHawk AWD", "Manual / Automatic", "5/7 Seater", 18);
        seedCar(mahindra, "XEV 9e", "Electric SUV Coupe", "INGLO Architecture Pure Electric (59 / 79 kWh)", "Automatic", "5 Seater", 7);
        seedCar(mahindra, "BE 6", "Electric Performance SUV", "INGLO Architecture Pure Electric", "Automatic", "5 Seater", 6);

        // 5. Kia
        CarBrand kia = seedBrand("Kia");
        seedCar(kia, "Sonet", "Compact SUV", "Smartstream Petrol / 1.5L CRDi Diesel", "Manual / iMT / 7-DCT / 6-AT", "5 Seater", 10);
        seedCar(kia, "Seltos", "Mid-size SUV", "1.5L Petrol / 1.5L Turbo / 1.5L Diesel", "Manual / IVT / 7-DCT / 6-AT", "5 Seater", 19);
        seedCar(kia, "Carens", "Family MPV", "1.5L Petrol / 1.5L Turbo / 1.5L Diesel", "Manual / iMT / 7-DCT / 6-AT", "6/7 Seater", 11);
        seedCar(kia, "Carens Clavis", "Premium SUV/MPV", "1.5L Turbo Petrol / 1.5L Diesel", "Automatic", "6/7 Seater", 5);
        seedCar(kia, "Syros", "Compact SUV", "Petrol / Turbo / EV", "Manual / Automatic", "5 Seater", 7);
        seedCar(kia, "EV6", "Luxury Electric Crossover", "All-Electric (77.4 kWh AWD)", "Automatic", "5 Seater", 4);
        seedCar(kia, "EV9", "Flagship Electric SUV", "All-Electric (99.8 kWh Dual Motor AWD)", "Automatic", "6 Seater", 3);
        seedCar(kia, "Carnival", "Limousine Luxury MPV", "2.2L Smartstream Diesel", "8-Speed Automatic", "7 Seater", 5);

        // 6. Toyota
        CarBrand toyota = seedBrand("Toyota");
        seedCar(toyota, "Glanza", "Premium Hatchback", "1.2L K-Series Petrol / E-CNG", "Manual / AMT", "5 Seater", 6);
        seedCar(toyota, "Taisor", "Compact Crossover", "1.2L Petrol / 1.0L Turbo / CNG", "Manual / AMT / 6-AT", "5 Seater", 7);
        seedCar(toyota, "Rumion", "MPV", "1.5L K-Series Petrol / CNG", "Manual / 6-AT", "7 Seater", 5);
        seedCar(toyota, "Urban Cruiser Hyryder", "Mid-size SUV", "Self-Charging Strong Hybrid / Neo Drive", "e-CVT / Manual / 6-AT", "5 Seater", 13);
        seedCar(toyota, "Innova Crysta", "Premium MPV", "2.4L GD Diesel", "5-Speed Manual", "7/8 Seater", 15);
        seedCar(toyota, "Innova Hycross", "Monocoque Hybrid MPV", "2.0L TNGA Strong Hybrid / Petrol", "e-Drive / CVT", "7/8 Seater", 22);
        seedCar(toyota, "Fortuner", "Full-size 4x4 SUV", "2.8L Diesel / 2.7L Petrol 4x4", "Manual / Automatic", "7 Seater", 16);
        seedCar(toyota, "Hilux", "Lifestyle 4x4 Pickup", "2.8L Diesel 4x4", "Manual / Automatic", "5 Seater", 4);
        seedCar(toyota, "Camry", "Luxury Hybrid Sedan", "2.5L Dynamic Force Self-Charging Hybrid", "e-CVT", "5 Seater", 5);
        seedCar(toyota, "Vellfire", "Ultra-Luxury MPV", "2.5L Strong Hybrid Electric AWD", "e-CVT", "7 Seater", 3);
        seedCar(toyota, "Land Cruiser 300", "Flagship Luxury 4x4", "3.3L Twin-Turbo V6 Diesel", "10-Speed Automatic", "5/7 Seater", 4);

        // 7. Honda
        CarBrand honda = seedBrand("Honda");
        seedCar(honda, "Amaze", "Compact Sedan", "1.2L i-VTEC Petrol", "Manual / CVT", "5 Seater", 8);
        seedCar(honda, "City", "Executive Sedan", "1.5L i-VTEC Petrol / e:HEV Strong Hybrid", "Manual / CVT / e-CVT", "5 Seater", 15);
        seedCar(honda, "Elevate", "Mid-size SUV", "1.5L i-VTEC Petrol", "6-Speed Manual / 7-Speed CVT", "5 Seater", 11);

        // 8. MG Motor
        CarBrand mg = seedBrand("MG Motor");
        seedCar(mg, "Comet EV", "Urban Smart EV", "All-Electric (17.3 kWh)", "Automatic", "4 Seater", 5);
        seedCar(mg, "Windsor EV", "Electric Crossover Lounge", "All-Electric (38 kWh Aero Lounge)", "Automatic", "5 Seater", 16);
        seedCar(mg, "ZS EV", "Electric SUV", "All-Electric (50.3 kWh)", "Automatic", "5 Seater", 8);
        seedCar(mg, "Hector", "Internet Mid SUV", "1.5L Turbo Petrol / 2.0L Diesel", "Manual / CVT", "5 Seater", 12);
        seedCar(mg, "Hector Plus", "6/7-Seater SUV", "1.5L Turbo Petrol / 2.0L Diesel", "Manual / CVT", "6/7 Seater", 7);
        seedCar(mg, "Gloster", "Full-size Luxury SUV", "2.0L Twin-Turbo Diesel 4x4", "8-Speed Automatic", "6/7 Seater", 4);
        seedCar(mg, "Cyberster", "Electric Sports Roadster", "All-Electric Dual Motor (503 hp)", "Automatic", "2 Seater", 3);

        // 9. Skoda
        CarBrand skoda = seedBrand("Skoda");
        seedCar(skoda, "Kylaq", "Sub-compact SUV", "1.0L TSI Petrol", "6-Speed Manual / 6-Speed AT", "5 Seater", 12);
        seedCar(skoda, "Kushaq", "Mid-size SUV", "1.0L TSI / 1.5L TSI EVO", "Manual / 6-AT / 7-DSG", "5 Seater", 9);
        seedCar(skoda, "Slavia", "Premium Sedan", "1.0L TSI / 1.5L TSI EVO", "Manual / 6-AT / 7-DSG", "5 Seater", 10);
        seedCar(skoda, "Kodiaq", "7-Seater Luxury SUV", "2.0L TSI Petrol 4x4", "7-Speed DSG", "7 Seater", 6);
        seedCar(skoda, "Octavia RS", "Performance Sedan", "2.0L TSI (265 hp)", "7-Speed DSG", "5 Seater", 7);

        // 10. Volkswagen
        CarBrand vw = seedBrand("Volkswagen");
        seedCar(vw, "Taigun", "Compact SUV", "1.0L TSI / 1.5L TSI EVO", "Manual / 6-AT / 7-DSG", "5 Seater", 10);
        seedCar(vw, "Virtus", "Dynamic Sedan", "1.0L TSI / 1.5L TSI EVO GT", "Manual / 6-AT / 7-DSG", "5 Seater", 13);
        seedCar(vw, "Tiguan R-Line", "Premium 4Motion SUV", "2.0L TSI 4Motion AWD", "7-Speed DSG", "5 Seater", 5);
        seedCar(vw, "Golf GTI", "Hot Hatchback", "2.0L TSI Turbocharged", "7-Speed DSG", "5 Seater", 6);

        // 11. Renault
        CarBrand renault = seedBrand("Renault");
        seedCar(renault, "Kwid", "Compact Hatchback", "1.0L SCe Petrol", "Manual / AMT", "5 Seater", 4);
        seedCar(renault, "Triber", "Modular 7-Seater MPV", "1.0L Energy Petrol", "Manual / AMT", "7 Seater", 6);
        seedCar(renault, "Kiger", "Compact SUV", "1.0L Energy / 1.0L Turbo Petrol", "Manual / AMT / X-TRONIC CVT", "5 Seater", 7);

        // 12. Nissan
        CarBrand nissan = seedBrand("Nissan");
        seedCar(nissan, "Magnite", "Compact SUV", "1.0L B4D Petrol / 1.0L Turbo", "Manual / EZ-Shift / CVT", "5 Seater", 8);
        seedCar(nissan, "X-Trail", "Premium 7-Seater SUV", "1.5L Variable Compression Turbo (VC-Turbo)", "CVT", "7 Seater", 3);

        // 13. Citroen
        CarBrand citroen = seedBrand("Citroen");
        seedCar(citroen, "C3", "Hatchback Crossover", "1.2L PureTech Petrol / Turbo", "Manual / 6-Speed AT", "5 Seater", 4);
        seedCar(citroen, "Aircross", "5+2 Seater Mid SUV", "1.2L Gen-3 Turbo Petrol", "Manual / 6-Speed AT", "5/7 Seater", 5);
        seedCar(citroen, "Basalt", "SUV Coupe", "1.2L PureTech 110 Turbo Petrol", "Manual / 6-Speed AT", "5 Seater", 6);
        seedCar(citroen, "eC3", "Electric Hatchback", "All-Electric (29.2 kWh)", "Automatic", "5 Seater", 4);

        // 14. BMW
        CarBrand bmw = seedBrand("BMW");
        seedCar(bmw, "2 Series Gran Coupe", "Compact Luxury Sedan", "2.0L TwinPower Turbo Petrol / Diesel", "7-Speed Steptronic", "5 Seater", 5);
        seedCar(bmw, "3 Series", "Executive Sports Sedan", "2.0L TwinPower Turbo Gran Limousine", "8-Speed Steptronic Sport", "5 Seater", 9);
        seedCar(bmw, "5 Series", "Long Wheelbase Luxury Sedan", "2.0L Mild-Hybrid TwinPower Turbo", "8-Speed Steptronic", "5 Seater", 7);
        seedCar(bmw, "7 Series", "Flagship Ultra-Luxury Sedan", "3.0L In-Line 6-Cylinder TwinPower Turbo", "8-Speed Steptronic", "5 Seater", 4);
        seedCar(bmw, "X1", "Luxury Compact SUV", "2.0L TwinPower Turbo sDrive", "7-Speed Dual Clutch", "5 Seater", 8);
        seedCar(bmw, "X3", "Luxury Mid-size SUV", "2.0L / 3.0L xDrive AWD", "8-Speed Steptronic", "5 Seater", 6);
        seedCar(bmw, "X5", "Executive Luxury SUV", "3.0L TwinPower Turbo xDrive", "8-Speed Steptronic Sport", "5/7 Seater", 7);
        seedCar(bmw, "X7", "Flagship 3-Row Luxury SUV", "3.0L In-Line 6 TwinPower Turbo xDrive", "8-Speed Steptronic", "6/7 Seater", 5);
        seedCar(bmw, "iX1", "Electric Luxury SUV", "All-Electric (66.4 kWh Dual Motor AWD)", "Automatic", "5 Seater", 6);
        seedCar(bmw, "i7", "Flagship Electric Luxury Sedan", "All-Electric (101.7 kWh Dual Motor xDrive60)", "Automatic", "5 Seater", 3);

        // 15. Mercedes-Benz
        CarBrand mb = seedBrand("Mercedes-Benz");
        seedCar(mb, "A-Class Limousine", "Entry Luxury Sedan", "1.3L Turbo Petrol / 2.0L Diesel", "7/8-Speed Dual Clutch", "5 Seater", 5);
        seedCar(mb, "C-Class", "Luxury Executive Sedan", "1.5L Turbo Petrol / 2.0L Diesel Mild-Hybrid", "9G-TRONIC", "5 Seater", 8);
        seedCar(mb, "E-Class", "Long Wheelbase Luxury Sedan", "2.0L Turbo Petrol / Diesel Mild-Hybrid", "9G-TRONIC", "5 Seater", 10);
        seedCar(mb, "S-Class", "Flagship Benchmark Luxury Sedan", "3.0L In-Line 6 Turbo 4MATIC", "9G-TRONIC", "5 Seater", 6);
        seedCar(mb, "GLA", "Compact Luxury SUV", "1.3L Petrol / 2.0L Diesel 4MATIC", "8G-DCT", "5 Seater", 7);
        seedCar(mb, "GLC", "Mid-size Luxury SUV", "2.0L Turbo 4MATIC Mild-Hybrid", "9G-TRONIC", "5 Seater", 9);
        seedCar(mb, "GLE", "Executive Luxury SUV", "2.0L / 3.0L 4MATIC Mild-Hybrid", "9G-TRONIC", "5 Seater", 7);
        seedCar(mb, "GLS", "The S-Class of SUVs", "3.0L Turbo 4MATIC Mild-Hybrid", "9G-TRONIC", "7 Seater", 6);
        seedCar(mb, "G-Class", "Iconic Luxury Off-Roader", "G 400d / AMG G 63 V8 Biturbo", "9G-TRONIC / AMG SPEEDSHIFT", "5 Seater", 5);
        seedCar(mb, "EQS", "Flagship Electric Luxury Sedan", "All-Electric (107.8 kWh 4MATIC)", "Automatic", "5 Seater", 4);

        // 16. Audi
        CarBrand audi = seedBrand("Audi");
        seedCar(audi, "A4", "Premium Sports Sedan", "2.0L 40 TFSI (204 hp)", "7-Speed S tronic", "5 Seater", 7);
        seedCar(audi, "A6", "Executive Luxury Sedan", "2.0L 45 TFSI (245 hp)", "7-Speed S tronic", "5 Seater", 6);
        seedCar(audi, "Q3", "Compact Luxury SUV", "2.0L 40 TFSI quattro", "7-Speed S tronic", "5 Seater", 8);
        seedCar(audi, "Q5", "Mid-size Luxury SUV", "2.0L 45 TFSI quattro", "7-Speed S tronic", "5 Seater", 7);
        seedCar(audi, "Q7", "7-Seater Luxury SUV", "3.0L 55 TFSI quattro (340 hp)", "8-Speed Tiptronic", "7 Seater", 5);
        seedCar(audi, "Q8", "Flagship Luxury SUV Coupe", "3.0L 55 TFSI quattro", "8-Speed Tiptronic", "5 Seater", 4);
        seedCar(audi, "e-tron GT", "Electric Performance GT", "All-Electric Dual Motor quattro (530 hp)", "2-Speed Automatic", "4 Seater", 3);

        // 17. Volvo
        CarBrand volvo = seedBrand("Volvo");
        seedCar(volvo, "XC60", "Mid-size Luxury SUV", "2.0L B5 Petrol Mild-Hybrid AWD", "8-Speed Geartronic", "5 Seater", 7);
        seedCar(volvo, "XC90", "7-Seater Luxury Flagship SUV", "2.0L B6 Petrol Mild-Hybrid AWD", "8-Speed Geartronic", "7 Seater", 6);
        seedCar(volvo, "EX30", "Compact Pure Electric SUV", "All-Electric (69 kWh Extended Range)", "Single-Speed", "5 Seater", 5);
        seedCar(volvo, "EX40", "Pure Electric Compact SUV", "All-Electric Twin Motor AWD (78 kWh)", "Single-Speed", "5 Seater", 6);
        seedCar(volvo, "EC40", "Pure Electric Crossover Coupe", "All-Electric Twin Motor AWD", "Single-Speed", "5 Seater", 4);

        // 18. BYD
        CarBrand byd = seedBrand("BYD");
        seedCar(byd, "Atto 3", "Electric Dynamic SUV", "All-Electric Blade Battery (60.48 kWh)", "Automatic", "5 Seater", 9);
        seedCar(byd, "Seal", "Electric Performance Sedan", "All-Electric Blade Battery (82.5 kWh AWD)", "Automatic", "5 Seater", 11);
        seedCar(byd, "eMAX 7", "Electric Premium MPV", "All-Electric Blade Battery (71.8 kWh)", "Automatic", "6/7 Seater", 7);
        seedCar(byd, "Sealion 7", "Electric Performance Crossover", "All-Electric Blade Battery (82.5 kWh)", "Automatic", "5 Seater", 6);

        // 19. Jeep
        CarBrand jeep = seedBrand("Jeep");
        seedCar(jeep, "Compass", "Premium Compact SUV", "2.0L Multijet II Diesel", "6-Speed Manual / 9-Speed AT", "5 Seater", 8);
        seedCar(jeep, "Meridian", "7-Seater Premium SUV", "2.0L Multijet II Diesel 4x4", "9-Speed Automatic", "7 Seater", 7);
        seedCar(jeep, "Wrangler", "Legendary 4x4 Off-Roader", "2.0L Turbo Petrol 4x4 Rock-Trac", "8-Speed Automatic", "5 Seater", 6);
        seedCar(jeep, "Grand Cherokee", "Flagship Luxury SUV", "2.0L Turbo Petrol Quadra-Trac I", "8-Speed Automatic", "5 Seater", 4);

        // 20. Land Rover
        CarBrand landRover = seedBrand("Land Rover");
        seedCar(landRover, "Defender", "Iconic All-Terrain 4x4 SUV", "2.0L / 3.0L Ingenium AWD Terrain Response", "8-Speed Automatic", "5/7 Seater", 12);
        seedCar(landRover, "Discovery Sport", "Compact 5+2 Luxury SUV", "2.0L Turbo Petrol / Diesel AWD", "9-Speed Automatic", "5+2 Seater", 5);
        seedCar(landRover, "Discovery", "Full-size 7-Seater Luxury SUV", "3.0L Ingenium Diesel / Petrol AWD", "8-Speed Automatic", "7 Seater", 4);
        seedCar(landRover, "Range Rover Evoque", "Compact Luxury SUV", "2.0L Ingenium Petrol / Diesel AWD", "9-Speed Automatic", "5 Seater", 6);
        seedCar(landRover, "Range Rover Velar", "Avant-Garde Luxury SUV", "2.0L Ingenium Petrol / Diesel AWD", "8-Speed Automatic", "5 Seater", 5);
        seedCar(landRover, "Range Rover Sport", "Performance Luxury SUV", "3.0L Ingenium MHEV Dynamic AWD", "8-Speed Automatic", "5 Seater", 8);
        seedCar(landRover, "Range Rover", "Flagship Pinnacle Luxury SUV", "3.0L / 4.4L V8 Twin-Turbo AWD", "8-Speed Automatic", "4/5/7 Seater", 9);

        log.info("Database seeding successfully completed for all 20 automobile brands and models requested.");
    }

    private CarBrand seedBrand(String name) {
        return brandRepository.save(new CarBrand(name, null, true));
    }

    private void seedCar(CarBrand brand, String modelName, String bodyType, String fuelType,
                         String transmission, String seating, int initialSampleRegistrations) {
        String description = String.format(
            "Experience the %s by %s. Join Bangalore car buyers negotiating volume collective pricing, corporate deals, and dealer benefits with authorized showrooms.",
            modelName, brand.getName()
        );

        String imageUrl = String.format(
            "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80"
        );

        Car car = carRepository.save(new Car(
            brand, modelName, imageUrl, bodyType, description, fuelType, transmission, seating, true
        ));

        String[] cities = {"Bengaluru", "Mumbai", "Delhi NCR", "Hyderabad", "Pune", "Chennai"};
        String[] timelines = {"Within 30 days", "1–3 months", "Immediate (< 15 days)", "> 3 months"};
        String[] firstNames = {"Aarav", "Priya", "Rohan", "Sneha", "Vikram", "Ananya", "Karthik", "Pooja", "Deepak", "Neha", "Siddharth", "Tanvi", "Aditya", "Meera"};
        String[] lastNames = {"Sharma", "Patel", "Mehta", "Iyer", "Malhotra", "Roy", "Raman", "Reddy", "Joshi", "Verma", "Nair", "Desai", "Kulkarni", "Sen"};
        LeadStatus[] statuses = {LeadStatus.JOINED, LeadStatus.CONTACTED, LeadStatus.WAITING, LeadStatus.BOUGHT, LeadStatus.CANCELLED};

        // Seed sample registrations to reflect initial collective group interest
        for (int i = 1; i <= initialSampleRegistrations; i++) {
            String samplePhone = String.format("98%02d%02d%04d", brand.getId() % 100, (car.getId() != null ? car.getId() % 100 : 1), i);
            String sampleEmail = String.format("buyer.b%d.c%d.u%d@carcrowd.in", brand.getId(), car.getId() != null ? car.getId() : 1, i);
            String name = firstNames[(i + (int)(car.getId() != null ? car.getId() : 1)) % firstNames.length] + " " +
                          lastNames[(i * 3) % lastNames.length];
            String city = cities[(i + (int)(car.getId() != null ? car.getId() : 1)) % cities.length];
            String timeline = timelines[i % timelines.length];
            LeadStatus status = statuses[i % statuses.length];

            InterestRegistration reg = new InterestRegistration(
                car,
                name,
                samplePhone,
                sampleEmail,
                25 + ((i * 7) % 35),
                city,
                timeline,
                true
            );
            reg.setLeadStatus(status);

            if (status != LeadStatus.JOINED) {
                reg.setLastContactedAt(java.time.LocalDateTime.now().minusDays(i % 5));
                reg.setNextFollowUpDate(java.time.LocalDate.now().plusDays((i % 7) + 1));
                reg.setInternalNotes(status == LeadStatus.BOUGHT
                    ? "Customer completed purchase and bought the vehicle with group savings."
                    : (status == LeadStatus.WAITING
                        ? "Customer confirmed interest in group deal. Waiting for dealership quota."
                        : (status == LeadStatus.CANCELLED ? "Customer postponed purchase plans." : "Followed up regarding color and delivery timeline.")));
            }

            InterestRegistration savedReg = interestRepository.save(reg);

            // 1. Initial creation timeline event
            activityRepository.save(new LeadActivity(
                savedReg.getId(),
                null,
                null,
                "REQUEST_CREATED",
                null,
                "JOINED",
                "Request submitted. Customer joined group for " + car.getModelName()
            ));

            // 2. Subsequent status change timeline event if progressed
            if (status != LeadStatus.JOINED) {
                activityRepository.save(new LeadActivity(
                    savedReg.getId(),
                    "admin@carcrowd.in",
                    "CarCrowd Operations Admin",
                    "STATUS_CHANGE",
                    "JOINED",
                    status.name(),
                    status == LeadStatus.BOUGHT
                        ? "Deal completed successfully! Customer purchased car with group discount."
                        : (status == LeadStatus.WAITING
                            ? "Interest confirmed via phone call. Moved to WAITING."
                            : (status == LeadStatus.CANCELLED ? "Customer declined to proceed. Request marked CANCELLED." : "Team contacted customer to verify interest."))
                ));
            }
        }
    }
}
