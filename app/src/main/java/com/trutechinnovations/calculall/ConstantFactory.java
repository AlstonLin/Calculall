package com.trutechinnovations.calculall;

/**
 * Created by Ejaaz on 2/7/2015.
 */
public class ConstantFactory {


    public static Constant makeSpeedOfLight() {
        return new Constant("Speed of Light", "c", 299792458, "m/s") {
            @Override
            public String toLaTeX() {
                return "c";
            }
        };
    }

    public static Constant makePlanck() {
        return new Constant("Planck constant", "h", 6.62606957e-34, "J·s") {
            @Override
            public String toLaTeX() {
                return "h";
            }
        };
    }

    public static Constant makeRedPlanck() {
        return new Constant("Planck constant", "ħ", 1.054571726e-34, "J·s") {
            @Override
            public String toLaTeX() {
                return "ħ";
            }
        };
    }

    public static Constant makeGravitational() {
        return new Constant("Gravitational constant", "G", 6.67384e-11, "N·m<sup>2</sup>/kg<sup>2</sup>") {
            @Override
            public String toLaTeX() {
                return "G";
            }
        };
    }

    public static Constant makeGasConst() {
        return new Constant("Molar gas constant", "R", 8.3144621, "J/mol·K") {
            @Override
            public String toLaTeX() {
                return "R";
            }
        };
    }

    public static Constant makeBoltzmann() {
        return new Constant("Boltzmann's constant", "k_B", 1.3806488e-23, "J/K") {
            @Override
            public String toLaTeX() {
                return "k_{B}";
            }
        };
    }

    public static Constant makeAvogadro() {
        return new Constant("Avogadro's Number", "N_A", 6.02214129e23, "mol<sup>-1</sup>") {
            @Override
            public String toLaTeX() {
                return "N_{A}";
            }
        };
    }

    public static Constant makeStefanBoltzmann() {
        return new Constant("Stefan-Boltzmann constant", "σ", 5.670373e-8, "W·m<sup>-2</sup>·K<sup>-4</sup>") {
            @Override
            public String toLaTeX() {
                return "\\sigma";
            }
        };
    }

    public static Constant makeFaraday() {
        return new Constant("Faraday constant", "F", 96485.3365, "C/mol") {
            @Override
            public String toLaTeX() {
                return "F";
            }
        };
    }

    public static Constant makeMagnetic() {
        return new Constant("Vacuum Permeability", "µ₀", Math.PI * (4e-7), "N/A<sup>2</sup>") {
            @Override
            public String toLaTeX() {
                return "\\mu_{0}";
            }
        };
    }

    public static Constant makeElectric() {
        return new Constant("Vacuum Permittivity", "ε₀", 8.854187817e-12, "F/m") {
            @Override
            public String toLaTeX() {
                return "\\epsilon_{0}";
            }
        };
    }

    public static Constant makeCoulomb() {
        return new Constant("Coulomb's Constant", "k", 8.987551787e9, "N·m<sup>2</sup>/C<sup>2</sup>") {
            @Override
            public String toLaTeX() {
                return "k_{e}";
            }
        };
    }

    public static Constant makeElemCharge() {
        return new Constant("Elementary Charge", "e", 1.602176565e-19, "C") {
            @Override
            public String toLaTeX() {
                return "e";
            }
        };
    }

    public static Constant makeElectronMass() {
        return new Constant("Electron Mass", "m_e", 9.10938291e-31, "kg") {
            @Override
            public String toLaTeX() {
                return "m_e";
            }
        };
    }

    public static Constant makeProtonMass() {
        return new Constant("Proton Mass", "m_p", 1.672621777e-27, "kg") {
            @Override
            public String toLaTeX() {
                return "m_p";
            }
        };
    }

    public static Constant makeNeutronMass() {
        return new Constant("Neutron Mass", "m_n", 1.674927351e-27, "kg") {
            @Override
            public String toLaTeX() {
                return "m_n";
            }
        };
    }

    public static Constant makeAtomicMass() {
        return new Constant("Atomic Mass constant", "u", 1.660538921e-27, "kg") {
            @Override
            public String toLaTeX() {
                return "u";
            }
        };
    }

    public static Constant makeRydberg() {
        return new Constant("Rydberg constant", "R", 10973731.568539, "m<sup>-1</sup>") {
            @Override
            public String toLaTeX() {
                return "R_{\\infty}";
            }
        };
    }

    public static Constant makeFineStruct() {
        return new Constant("Fine-structure constant", "α", 7.2973525698e-3, "") {
            @Override
            public String toLaTeX() {
                return "\\alpha";
            }
        };
    }

    public static Constant makeEarthGrav() {
        return new Constant("Surface gravity of Earth", "g", 9.80665, "m/s<sup>2</sup>") {
            @Override
            public String toLaTeX() {
                return "g_{\\Earth}";
            }
        };
    }

    public static Constant makeEarthMass() {
        return new Constant("Mass of Earth", "M_e", 5.97219e24, "kg") {
            @Override
            public String toLaTeX() {
                return "M_{\\Earth}";
            }
        };
    }

    public static Constant makeEarthRadius() {
        return new Constant("Mean radius of Earth", "R_e", 6371000, "m") {
            @Override
            public String toLaTeX() {
                return "R_{\\Earth}";
            }
        };
    }

    public static Constant makeSolarMass() {
        return new Constant("Mass of the Sun", "M_s", 1.98855e30, "kg") {
            @Override
            public String toLaTeX() {
                return "M_{\\Sun}";
            }
        };
    }

    public static Constant makeSolarRadius() {
        return new Constant("Radius of the Sun", "R_s", 6.955e8, "m") {
            @Override
            public String toLaTeX() {
                return "R_{\\Sun}";
            }
        };
    }

    public static Constant makeSolarLuminosity() {
        return new Constant("Luminosity of the Sun", "L_s", 3.846e26, "W") {
            @Override
            public String toLaTeX() {
                return "L_{\\Sun";
            }
        };
    }

    public static Constant makeAU() {
        return new Constant("Astronomical Unit", "au", 1.495978707e11, "m") {
            @Override
            public String toLaTeX() {
                return "AU";
            }
        };
    }

    public static Constant makePhi() {
        return new Constant("The Golden Ratio", "ϕ", 1.61803398874, "") {
            @Override
            public String toLaTeX() {
                return "\\phi";
            }
        };
    }

    public static Constant makeEulerMascheroni() {
        return new Constant("Euler-Mascheroni constant", "γ", 0.577215664901532860606512, "") {
            @Override
            public String toLaTeX() {
                return "\\gamma";
            }
        };
    }
}
