package interfaz;

import entidad.*;
import control.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.File;

public class GestionGimnasio {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ControlClientes controlClientes = new ControlClientes();
        ControlActividades controlActividades = new ControlActividades();
        ControlRutinas controlRutinas = new ControlRutinas();
        ControlPagos controlPagos = new ControlPagos();

        int opcion;
        do {
            System.out.println("\n=== Sistema de Gestión de Gimnasio ===");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Asignar/Crear rutina a cliente");
            System.out.println("4. Registrar pago");
            System.out.println("5. Listar/Agregar actividades");
            System.out.println("6. Listar/Crear rutinas");
            System.out.println("7. Listar pagos");
            System.out.println("8. Exportar rutina CSV");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            opcion = leerInt(sc);

            switch (opcion) {
                case 1 -> registrarCliente(sc, controlClientes, controlActividades, controlRutinas, controlPagos);
                case 2 -> controlClientes.listarClientes();
                case 3 -> asignarOCrearRutina(sc, controlClientes, controlRutinas);
                case 4 -> registrarPagoPorCliente(sc, controlClientes, controlPagos);
                case 5 -> menuActividades(sc, controlActividades);
                case 6 -> menuRutinas(sc, controlRutinas);
                case 7 -> controlPagos.listarPagos();
                case 8 -> exportarRutinaMenu(sc, controlRutinas);
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);

        sc.close();
    }

    private static int leerInt(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (Exception ex) {
            return -1;
        }
    }

    private static void menuActividades(Scanner sc, ControlActividades controlActividades) {
        while (true) {
            controlActividades.listarActividades();
            System.out.println("0. Agregar nueva actividad");
            System.out.println("-1. Volver");
            System.out.print("Opción: ");
            int op = leerInt(sc);
            if (op == -1) break;

            if (op == 0) {
                System.out.print("Nombre de la actividad: ");
                String nombre = sc.nextLine().trim();
                System.out.print("Precio: ");
                double precio = Double.parseDouble(sc.nextLine().trim());
                System.out.print("Duración (días): ");
                int duracion = Integer.parseInt(sc.nextLine().trim());
                controlActividades.agregarActividad(
                        new Actividades(controlActividades.getActividades().size() + 1, nombre, precio, duracion)
                );
                System.out.println("✅ Actividad agregada: " + nombre);
            } else {
                System.out.println("ID seleccionado: " + op);
            }
        }
    }

    private static void menuRutinas(Scanner sc, ControlRutinas controlRutinas) {
        while (true) {
            controlRutinas.listarRutinas();
            System.out.println("0. Crear nueva rutina personalizada");
            System.out.println("-1. Volver");
            System.out.print("Opción: ");
            int op = leerInt(sc);
            if (op == -1) break;

            if (op == 0) {
                crearRutinaPersonalizada(sc, controlRutinas);
            } else {
                System.out.println("ID seleccionado: " + op);
            }
        }
    }

    private static void exportarRutinaMenu(Scanner sc, ControlRutinas controlRutinas) {
        controlRutinas.listarRutinas();
        System.out.print("Ingrese ID de rutina a exportar: ");
        int id = leerInt(sc);
        Rutinas r = controlRutinas.buscarRutinaPorId(id);

        if (r == null) {
            System.out.println("Rutina no encontrada.");
            return;
        }

        System.out.print("Ingrese ruta de archivo CSV (ej: C:\\temp\\rutina_" + id + ".csv): ");
        String ruta = sc.nextLine().trim();
        File file = new File(ruta);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();

        boolean ok = controlRutinas.exportarRutinaACSV(r, ruta);
        if (ok) System.out.println("✅ Exportado correctamente a " + ruta);
        else System.out.println("❌ Error al exportar la rutina.");
    }

    // -------- Métodos de clientes, pagos y rutinas --------

    private static void registrarCliente(Scanner sc, ControlClientes controlClientes, ControlActividades controlActividades,
                                         ControlRutinas controlRutinas, ControlPagos controlPagos) {

        System.out.println("\n--- Registrar Cliente ---");
        System.out.print("DNI: ");
        String dni = sc.nextLine().trim();

        if (!dni.matches("\\d+")) {
            System.out.println("❌ DNI inválido");
            return;
        }

        if (controlClientes.buscarClientePorDni(dni) != null) {
            System.out.println("❌ Cliente ya existe");
            return;
        }

        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = sc.nextLine().trim();
        System.out.print("Celular: ");
        String celular = sc.nextLine().trim();
        System.out.print("Correo: ");
        String correo = sc.nextLine().trim();
        System.out.print("Fecha Nac (dd/MM/yyyy): ");

        LocalDate fechaNac;
        try {
            fechaNac = LocalDate.parse(sc.nextLine().trim(), FORMATO);
        } catch (Exception ex) {
            System.out.println("❌ Fecha inválida");
            return;
        }

        controlActividades.listarActividades();
        System.out.print("Seleccione ID de actividad: ");
        int idAct = leerInt(sc);
        Actividades actividad = controlActividades.buscarActividadPorId(idAct);
        if (actividad == null) actividad = new Actividades(0, "Sin actividad", 0.0, actividad.getDuracion());

        Rutinas rutina;
        String actNameLower = actividad.getNombre().toLowerCase();
        if (actNameLower.contains("muscul") || actNameLower.contains("aparato") || actNameLower.contains("combo")) {
            rutina = seleccionarOCrearRutina(sc, controlRutinas);
        } else {
            rutina = new Rutinas(0, "No aplica", "Actividad sin rutina");
        }

        Pagos pago = new Pagos(
                controlPagos.getPagos().size() + 1,
                LocalDate.now(),
                actividad.getPrecio(),
                "Adeuda",
                nombre + " " + apellido
        );
        controlPagos.agregarPago(pago);

        Clientes nuevo = new Clientes(
                controlClientes.generarId(), dni, nombre, apellido, celular, correo, fechaNac, actividad, rutina, pago
        );


        controlClientes.agregarCliente(nuevo);
        System.out.println("✅ Cliente registrado: " + nombre + " " + apellido);
    }

    private static Rutinas seleccionarOCrearRutina(Scanner sc, ControlRutinas controlRutinas) {
        controlRutinas.listarRutinas();
        System.out.println("0. Crear rutina personalizada");
        System.out.print("Seleccione ID o 0 para crear nueva: ");
        int idRut = leerInt(sc);

        if (idRut == 0) return crearRutinaPersonalizada(sc, controlRutinas);

        Rutinas r = controlRutinas.buscarRutinaPorId(idRut);
        if (r == null) return new Rutinas(0, "Sin rutina", "No asignada");
        else return r;
    }

    private static Rutinas crearRutinaPersonalizada(Scanner sc, ControlRutinas controlRutinas) {
        System.out.print("Nombre rutina: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Descripción: ");
        String desc = sc.nextLine().trim();

        System.out.print("Fecha inicio (dd/MM/yyyy): ");
        LocalDate ini;
        try {
            ini = LocalDate.parse(sc.nextLine().trim(), FORMATO);
        } catch (Exception ex) {
            System.out.println("❌ Fecha inválida");
            return null;
        }

        System.out.print("Fecha fin (dd/MM/yyyy): ");
        LocalDate fin;
        try {
            fin = LocalDate.parse(sc.nextLine().trim(), FORMATO);
        } catch (Exception ex) {
            System.out.println("❌ Fecha inválida");
            return null;
        }

        Rutinas rutina = new Rutinas(controlRutinas.getRutinas().size() + 1, nombre, desc, ini, fin);
        int semanas = rutina.getSemanas();

        System.out.print("Cantidad de ejercicios: ");
        int nEj = leerInt(sc);
        if (nEj <= 0) nEj = 1;

        for (int i = 0; i < nEj; i++) {
            System.out.print("Ejercicio #" + (i + 1) + " nombre: ");
            String nomEj = sc.nextLine().trim();
            System.out.print("Series: ");
            int s = leerInt(sc);
            System.out.print("Reps: ");
            int r = leerInt(sc);

            System.out.print("Día del ejercicio (1-7): ");
            int dia = leerInt(sc);
            if (dia < 1 || dia > 7) dia = 1; // por defecto día 1

            Ejercicio ej = new Ejercicio(nomEj, Math.max(1, s), Math.max(1, r), dia, semanas);

            for (int sem = 0; sem < semanas; sem++) {
                System.out.print("Nota Semana " + (sem + 1) + " (enter omitir): ");
                String nota = sc.nextLine().trim();
                if (!nota.isEmpty()) ej.setNotaSemana(sem, nota);
            }
            rutina.agregarEjercicio(ej);
        }

        controlRutinas.getRutinas().add(rutina);
        System.out.println("✅ Rutina creada: " + nombre);
        return rutina;
    }

    private static void asignarOCrearRutina(Scanner sc, ControlClientes controlClientes, ControlRutinas controlRutinas) {
        System.out.print("DNI cliente: ");
        String dni = sc.nextLine().trim();
        Clientes c = controlClientes.buscarClientePorDni(dni);

        if (c == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        String actNameLower = c.getActividad().getNombre().toLowerCase();
        if (!(actNameLower.contains("muscul") || actNameLower.contains("aparato") || actNameLower.contains("combo"))) {
            System.out.println("⚠ La actividad no requiere rutina.");
            return;
        }

        Rutinas r = seleccionarOCrearRutina(sc, controlRutinas);
        if (r != null) {
            c.setRutina(r);
            System.out.println("Rutina asignada a " + c.getNombre());
        }
    }

    private static void registrarPagoPorCliente(Scanner sc, ControlClientes controlClientes, ControlPagos controlPagos) {
        System.out.print("Nombre: ");
        String nom = sc.nextLine().trim();
        System.out.print("Apellido: ");
        String ape = sc.nextLine().trim();

        Clientes c = controlClientes.getClientes().stream()
                .filter(cli -> cli.getNombre().equalsIgnoreCase(nom) && cli.getApellido().equalsIgnoreCase(ape))
                .findFirst()
                .orElse(null);

        if (c == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        Actividades act = c.getActividad();
        if (act == null) {
            System.out.println("Cliente no tiene actividad.");
            return;
        }

        double monto = act.getPrecio();
        System.out.print("Estado (Pagado/Adeuda): ");
        String estado = sc.nextLine().trim();

        if (!estado.equalsIgnoreCase("pagado") && !estado.equalsIgnoreCase("adeuda"))
            estado = "Adeuda";

        Pagos p = new Pagos(controlPagos.getPagos().size() + 1, LocalDate.now(), monto, estado, c.getNombre() + " " + c.getApellido());
        controlPagos.agregarPago(p);
        c.setPago(p);

        System.out.println("✅ Pago registrado");
    }
}
