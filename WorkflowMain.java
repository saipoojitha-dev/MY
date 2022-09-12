package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkflowMain {

	static List<Product> prods = Utility.products();

	static List<Orders> invOrders = new ArrayList<>();

	public static void main(String[] args) {

		List<Oppertunity> ops = Utility.oppertunity();

		System.out.println("Products in stock: " + prods);

		System.out.println("********* Total Oppertunities: " + ops.size() + "********");
		System.out.println("");
		System.out.println("--------- Oppertunity check starts ------------");

		ops.forEach(e -> {
			if (e.getStage().equalsIgnoreCase("Closed Won")) {
				System.out.println("******************* Oppertunity check start ********************");
				System.out.println("Oppertunity Name :" + e.getName());
				System.out.println("Oppertunity Status :" + e.getStage());
				checkStockAndUpdate(e);
			}
		});

		System.out.println("");
		System.out.println("Products left in inventory final :" + prods);
		System.out.println("");
		System.out.println("Inventory Order placed :" + invOrders);
		System.out.println("");
		System.out.println("Final Oppertunity status: " + ops);
	}

	private static void checkStockAndUpdate(Oppertunity op) {

		List<Product> plines = op.getProductline();

		plines.stream().forEach(e -> {

			prods.stream().forEach(a -> {

				if (a.getProductId().equalsIgnoreCase(e.getProductId())) {
					System.out.println("Name :" + a.getName() + "----- Available stock: " + a.getStock()
							+ "------ Required stock: " + e.getStock());
					if (a.getStock() < e.getStock()) {
						System.out.println("Stock not available!");
						op.setFinalStatus("Stock not available!");
						op.setConfirmationLabel("On Hold");
						System.out.println("");
						System.out.println("------ Preparing Inventory Order -----------");

						Optional<Orders> opt = invOrders.stream()
								.filter(s -> s.getProductId().equalsIgnoreCase(a.getProductId())).findAny();
						if (opt.isPresent()) {
							System.out.println("");
							System.out.println(
									"Order already placed for this product. Order Id: " + opt.get().getOrderid());
						} else {
							Orders od = new Orders();
							od.setOrderid(UUID.randomUUID().toString());
							od.setProductId(a.getProductId());
							od.setProductName(a.getName());

							invOrders.add(od);
							System.out.println("");
							System.out.println("Order placed! Product Id:" + od.getProductId());
						}
					} else {
						Integer dif = a.getStock() - e.getStock();
						a.setStock(dif);

						System.out.println("Stock inventory updated!");
						System.out.println("");
						op.setFinalStatus("Stock Ready to dispatch!");
						op.setConfirmationLabel("Success");

					}
				}
			});
		});
	}
}
