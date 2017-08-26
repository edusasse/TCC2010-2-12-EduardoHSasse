import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import libsvm.svm_node;

class MySvmScale {
	private static double lower = -1.0;
	private static double upper = 1.0;
	private static double y_lower;
	private static double y_upper;
	private static boolean y_scaling = false;
	private static double[] feature_max;
	private static double[] feature_min;
	private static double y_max = -Double.MAX_VALUE;
	private static double y_min = Double.MAX_VALUE;
	private static int max_index;
	private static long num_nonzeros = 0;
	private static long new_num_nonzeros = 0;
	private static String strPar = "";
	private static svm_node[] node = new svm_node[900];

	private static void output_target(double value) {
		if (y_scaling) {
			if (value == y_min)
				value = y_lower;
			else if (value == y_max)
				value = y_upper;
			else
				value = y_lower + (y_upper - y_lower) * (value - y_min)
						/ (y_max - y_min);
		}
	}

	private static void output(int index, double value) {
		/* skip single-valued attribute */
		if (feature_max[index] == feature_min[index])
			return;

		if (value == feature_min[index])
			value = lower;
		else if (value == feature_max[index])
			value = upper;
		else
			value = lower + (upper - lower) * (value - feature_min[index])
					/ (feature_max[index] - feature_min[index]);

		svm_node n = new svm_node();
		// System.out.println(index+"::"+value);
		n.index = index;
		n.value = value;
		node[index - 1] = n; // System.out.print(index + ":" + value + " ");
		new_num_nonzeros++;
	}

	public final static svm_node[] scale(String line) throws IOException {
		int i, index;
		node = new svm_node[900];
		BufferedReader fp = null, fp_restore = null;
		// Inicializa
		fp_restore = new BufferedReader(new StringReader(strPar));

		/* assumption: min index of attributes is 1 */
		/* pass 1: find out max index of attributes */
		max_index = 0;
		int idx, c;
		if ((c = fp_restore.read()) == 'y') {
			fp_restore.readLine();
			fp_restore.readLine();
			fp_restore.readLine();
		}
		fp_restore.readLine();
		fp_restore.readLine();

		String restore_line = null;
		while ((restore_line = fp_restore.readLine()) != null) {
			StringTokenizer st2 = new StringTokenizer(restore_line);
			idx = Integer.parseInt(st2.nextToken());
			max_index = Math.max(max_index, idx);
		}

		// Reinicia
		fp_restore = new BufferedReader(new StringReader(strPar));

		StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
		st.nextToken();
		while (st.hasMoreTokens()) {
			index = Integer.parseInt(st.nextToken());
			max_index = Math.max(max_index, index);
			st.nextToken();
			num_nonzeros++;
		}

		try {
			feature_max = new double[(max_index + 1)];
			feature_min = new double[(max_index + 1)];
		} catch (OutOfMemoryError e) {
			System.err.println("can't allocate enough memory");
			System.exit(1);
		}

		for (i = 0; i <= max_index; i++) {
			feature_max[i] = -Double.MAX_VALUE;
			feature_min[i] = Double.MAX_VALUE;
		}

		/* pass 2: find out min/max value */

		int next_index = 1;
		double target;
		double value;

		StringTokenizer st2 = new StringTokenizer(line, " \t\n\r\f:");
		target = Double.parseDouble(st2.nextToken());
		y_max = Math.max(y_max, target);
		y_min = Math.min(y_min, target);

		while (st2.hasMoreTokens()) {
			index = Integer.parseInt(st2.nextToken());
			value = Double.parseDouble(st2.nextToken());

			for (i = next_index; i < index; i++) {
				feature_max[i] = Math.max(feature_max[i], 0);
				feature_min[i] = Math.min(feature_min[i], 0);
			}

			feature_max[index] = Math.max(feature_max[index], value);
			feature_min[index] = Math.min(feature_min[index], value);
			next_index = index + 1;
		}

		for (i = next_index; i <= max_index; i++) {
			feature_max[i] = Math.max(feature_max[i], 0);
			feature_min[i] = Math.min(feature_min[i], 0);
		}

		/* pass 2.5: save/restore feature_min/feature_max */
		// fp_restore rewinded in finding max_index
		int idx2, c2;
		double fmin, fmax;

		fp_restore.mark(2); // for reset
		if ((c2 = fp_restore.read()) == 'y') {
			fp_restore.readLine(); // pass the '\n' after 'y'
			StringTokenizer st3 = new StringTokenizer(fp_restore.readLine());
			y_lower = Double.parseDouble(st3.nextToken());
			y_upper = Double.parseDouble(st3.nextToken());
			st3 = new StringTokenizer(fp_restore.readLine());
			y_min = Double.parseDouble(st3.nextToken());
			y_max = Double.parseDouble(st3.nextToken());
			y_scaling = true;
		} else
			fp_restore.reset();

		if (fp_restore.read() == 'x') {
			fp_restore.readLine(); // pass the '\n' after 'x'
			StringTokenizer st4 = new StringTokenizer(fp_restore.readLine());
			lower = Double.parseDouble(st4.nextToken());
			upper = Double.parseDouble(st4.nextToken());
			String restore_line2 = null;
			while ((restore_line2 = fp_restore.readLine()) != null) {
				StringTokenizer st5 = new StringTokenizer(restore_line2);
				idx2 = Integer.parseInt(st5.nextToken());
				fmin = Double.parseDouble(st5.nextToken());
				fmax = Double.parseDouble(st5.nextToken());
				if (idx2 <= max_index) {
					feature_min[idx2] = fmin;
					feature_max[idx2] = fmax;
				}
			}
		}
		// fp_restore.close();

		/* pass 3: scale */
		int next_index2 = 1;
		double target2;
		double value2;

		StringTokenizer st7 = new StringTokenizer(line, " \t\n\r\f:");
		target2 = Double.parseDouble(st7.nextToken());
		output_target(target2);
		while (st7.hasMoreElements()) {
			index = Integer.parseInt(st7.nextToken());
			value2 = Double.parseDouble(st7.nextToken());
			for (i = next_index2; i < index; i++)
				output(i, 0);
			output(index, value2);
			next_index2 = index + 1;
		}

		for (i = next_index2; i <= max_index; i++)
			output(i, 0);

		if (new_num_nonzeros > num_nonzeros)
			System.err.print("Warning: original #nonzeros " + num_nonzeros
					+ "\n" + "         new      #nonzeros " + new_num_nonzeros
					+ "\n"
					+ "Use -l 0 if many original feature values are zeros\n");

		// Retorna o svm-node pronto
		return node;
	}

	public static void loadParamters(File fpar) throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader(fpar));
		String str = "";
		while ((str = in.readLine()) != null)
			strPar += str + "\n";
		in.close();
	}
}
