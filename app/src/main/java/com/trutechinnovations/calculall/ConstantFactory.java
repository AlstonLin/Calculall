/*
 * Copyright (c) 2016 TruTech Innovations Inc - All Rights Reserved
 */

package com.trutechinnovations.calculall;

/**
 * A Factory that produces specific Mathematical and Physical Constants.
 *
 * @author Ejaaz Merali
 * @version 3.0
 */
public class ConstantFactory {

    public static Constant makeSpeedOfLight() {
        return new Constant("Speed of Light", "c", "c", 299792458, "m·s<sup><small>-1</small></sup>") {
            public String toLaTeX() {
                return "c";
            }
        };
    }

    public static Constant makePlanck() {
        return new Constant("Planck constant", "h", "h", 6.62606957e-34, "J·s") {
            public String toLaTeX() {
                return "h";
            }
        };
    }

    public static Constant makeRedPlanck() {
        return new Constant("Reduced Planck constant", "ħ", "ħ", 1.054571726e-34, "J·s") {
            public String toLaTeX() {
                return "ħ";
            }
        };
    }

    public static Constant makeGravitational() {
        return new Constant("Gravitational constant", "G", "G", 6.67384e-11, "N·m<sup><small>2</small></sup>·kg<sup><small>-2</small></sup>") {
            public String toLaTeX() {
                return "G";
            }
        };
    }

    public static Constant makeGasConst() {
        return new Constant("Molar gas constant", "R", "R", 8.3144621, "J·mol<sup><small>-1</small></sup>·K<sup><small>-1</small></sup>") {
            public String toLaTeX() {
                return "R";
            }
        };
    }

    public static Constant makeBoltzmann() {
        return new Constant(
                "Boltzmann's constant",
                "k<sub><small><small><small>B</small></small></small></sub>",
                "k☺B☺",
                1.3806488e-23,
                "J·K<sup><small>-1</small></sup>") {
            public String toLaTeX() {
                return "k_{B}";
            }
        };
    }

    public static Constant makeAvogadro() {
        return new Constant(
                "Avogadro's Number",
                "N<sub><small><small><small>A</small></small></small></sub>",
                "N☺A☺",
                6.02214129e23,
                "mol<sup><small>-1</small></sup>") {
            public String toLaTeX() {
                return "N_{A}";
            }
        };
    }

    public static Constant makeStefanBoltzmann() {
        return new Constant("Stefan-Boltzmann constant", "σ", "σ", 5.670373e-8, "W·m<sup><small>-2</small></sup>·K<sup><small>-4</small></sup>") {
            public String toLaTeX() {
                return "\\sigma";
            }
        };
    }

    public static Constant makeFaraday() {
        return new Constant("Faraday constant", "F", "F", 96485.3365, "C·mol<sup><small>-1</small></sup>") {
            public String toLaTeX() {
                return "F";
            }
        };
    }

    public static Constant makeMagnetic() {
        return new Constant(
                "Vacuum Permeability",
                "μ<sub><small><small><small>0</small></small></small></sub>",
                "μ☺0☺",
                Math.PI * (4e-7),
                "N·A<sup><small>-2</small></sup>") {
            public String toLaTeX() {
                return "\\mu_{0}";
            }
        };
    }

    public static Constant makeElectric() {
        return new Constant("Vacuum Permittivity",
                "ɛ<sub><small><small><small>0</small></small></small></sub>",
                "ɛ☺0☺",
                8.854187817e-12,
                "C·V<sup><small>-1</small></sup>·m<sup><small>-1</small></sup>") {
            public String toLaTeX() {
                return "\\epsilon_{0}";
            }
        };
    }

    public static Constant makeCoulomb() {
        return new Constant("Coulomb's Constant", "k", "k", 8.987551787e9, "N·m<sup><small>2</small></sup>·C<sup><small>-2</small></sup>") {
            public String toLaTeX() {
                return "k_{e}";
            }
        };
    }

    public static Constant makeElemCharge() {
        return new Constant("Elementary Charge", "e", "e", 1.602176565e-19, "C") {
            public String toLaTeX() {
                return "e";
            }
        };
    }

    public static Constant makeElectronVolt() {
        return new Constant("Electronvolt", "eV", "eV", 1.602176565e-19, "J") {
            public String toLaTeX() {
                return "eV";
            }
        };
    }

    public static Constant makeElectronMass() {
        return new Constant("Electron Mass",
                "m<sub><small><small><small>e</small></small></small></sub>",
                "m☺e☺",
                9.10938291e-31,
                "kg") {
            public String toLaTeX() {
                return "m_{e}";
            }
        };
    }

    public static Constant makeProtonMass() {
        return new Constant("Proton Mass",
                "m<sub><small><small><small>p</small></small></small></sub>",
                "m☺p☺",
                1.672621777e-27,
                "kg") {
            public String toLaTeX() {
                return "m_p";
            }
        };
    }

    public static Constant makeNeutronMass() {
        return new Constant("Neutron Mass",
                "m<sub><small><small><small>n</small></small></small></sub>",
                "m☺n☺",
                1.674927351e-27,
                "kg") {
            public String toLaTeX() {
                return "m_n";
            }
        };
    }

    public static Constant makeAtomicMass() {
        return new Constant("Atomic Mass constant", "u", "u", 1.660538921e-27, "kg") {

            public String toLaTeX() {
                return "u";
            }
        };
    }

    public static Constant makeBohrMagneton() {
        return new Constant(
                "Bohr magneton",
                "μ<sub><small><small><small>B</small></small></small></sub>",
                "μ☺B☺",
                9.27400968e-24,
                "J·T<sup><small>-1</small></sup>") {
            public String toLaTeX() {
                return "μ_{B}";
            }
        };
    }

    public static Constant makeBohrRadius() {
        return new Constant(
                "Bohr radius",
                "a<sub><small><small><small>0</small></small></small></sub>",
                "a☺0☺",
                5.2917721092e-11, "m") {
            public String toLaTeX() {
                return "a_{0}";
            }
        };
    }

    public static Constant makeRydberg() {
        return new Constant("Rydberg constant",
                "R<sub><small><small><small>∞</small></small></small></sub>",
                "R☺∞☺",
                10973731.568539, "m<sup><small>-1</small></sup>") {

            public String toLaTeX() {
                return "R_{\\infty}";
            }
        };
    }

    public static Constant makeFineStruct() {
        return new Constant("Fine-structure constant", "α", "α", 7.2973525698e-3, "") {

            public String toLaTeX() {
                return "\\alpha";
            }
        };
    }

    public static Constant makeMagneticFluxQuantum() {
        return new Constant(
                "Magnetic flux quantum",
                "Φ<sub><small><small><small>0</small></small></small></sub>",
                "Φ☺0☺",
                2.067833758e-15, "Wb") {
            public String toLaTeX() {
                return "\\phi_{0}";
            }
        };
    }

    public static Constant makeEarthGrav() {
        return new Constant("Surface gravity of Earth",
                "g<sub><small><small><small>⊕</small></small></small></sub>",
                "g☺⊕☺",
                9.80665, "m·s<sup><small>-2</small></sup>") {

            public String toLaTeX() {
                return "g_{\\Earth}";
            }
        };
    }

    public static Constant makeEarthMass() {
        return new Constant("Mass of Earth",
                "M<sub><small><small><small>⊕</small></small></small></sub>",
                "M☺⊕☺",
                5.97219e24, "kg") {
            public String toLaTeX() {
                return "M_{\\Earth}";
            }
        };
    }

    public static Constant makeEarthRadius() {
        return new Constant("Mean radius of Earth",
                "R<sub><small><small><small>⊕</small></small></small></sub>",
                "R☺⊕☺",
                6371009, "m") {

            public String toLaTeX() {
                return "R_{\\Earth}";
            }
        };
    }

    public static Constant makeSolarMass() {
        return new Constant("Mass of the Sun",
                "M<sub><small><small><small>⊙</small></small></small></sub>",
                "M☺⊙☺",
                1.98855e30, "kg") {

            public String toLaTeX() {
                return "M_{\\Sun}";
            }
        };
    }

    public static Constant makeSolarRadius() {
        return new Constant("Radius of the Sun",
                "R<sub><small><small><small>⊙</small></small></small></sub>",
                "R☺⊙☺",
                6.955e8, "m") {
            public String toLaTeX() {
                return "R_{\\Sun}";
            }
        };
    }

    public static Constant makeSolarLuminosity() {
        return new Constant("Luminosity of the Sun",
                "L<sub><small><small><small>⊙</small></small></small></sub>",
                "L☺⊙☺",
                3.846e26, "W") {
            public String toLaTeX() {
                return "L_{\\Sun";
            }
        };
    }

    public static Constant makeAU() {
        return new Constant("Astronomical Unit", "AU", "AU", 1.495978707e11, "m") {

            public String toLaTeX() {
                return "AU";
            }
        };
    }

    public static Constant makeLightYear() {
        return new Constant("Light Year", "ly", "ly", 9.4607304725808e15, "m") {

            public String toLaTeX() {
                return "ly";
            }
        };
    }

    public static Constant makePhi() {
        return new Constant("The Golden Ratio", "ϕ", "ϕ", 1.61803398874, "") {

            public String toLaTeX() {
                return "\\phi";
            }
        };
    }

    public static Constant makeEulerMascheroni() {
        return new Constant("Euler-Mascheroni constant", "γ", "γ", 0.577215664901532860606512, "") {

            public String toLaTeX() {
                return "\\gamma";
            }
        };
    }
}
