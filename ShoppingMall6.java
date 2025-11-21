import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.*;

import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; 

class BlinkitCollection1 {
	public static double calculateTotalBill(Map<String, Integer> cart, Map<String, Double> items) {
       		double total = 0.0;
       		for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            		String itemName = entry.getKey();
           		int quantity = entry.getValue();
           		double price = items.get(itemName);
           	 	total += price * quantity;
       		}
        	return total;
   	 }
	
	public static void main(String[] args) {

		System.out.println("Welcome To The Blinkit Store");

		Scanner scanner = new Scanner(System.in);
						
		System.out.println("Enter a date to check bill summary (yyyy-MM-dd):");
		String inputDate = scanner.nextLine();

		LocalDate parsedDate;
		try {
    			parsedDate = LocalDate.parse(inputDate); // Validates format
		} catch (DateTimeParseException e) {
  			System.out.println(" Invalid date format. Please use yyyy-MM-dd.");
    			return;
		}

		// Base directory - use absolute path to avoid confusion
		String basePath = "C:/Users/User21/Desktop/java_practise/Bills";

		// Today's date
		LocalDate today = LocalDate.now();
		String year = String.valueOf(parsedDate.getYear());
		String month = String.format("%02d", parsedDate.getMonthValue());
		String day = String.format("%02d", parsedDate.getDayOfMonth());

		// Construct full folder path
		String folderPath = basePath + "/" + year + "/" + month + "/" + day;

		File billFolder = new File(folderPath);

		if (!billFolder.exists()) {
    			boolean created = billFolder.mkdirs();
    			if (created) {
        			System.out.println(" Folder created: " + billFolder.getAbsolutePath());
    			} else {
        			System.out.println(" Failed to create folder at: " + billFolder.getAbsolutePath());
    			}
		} else {
 			System.out.println(" Folder already exists: " + billFolder.getAbsolutePath());
		}

		File dateFolder = new File(folderPath);

		if (!dateFolder.exists() || !dateFolder.isDirectory()) {
    			System.out.println(" No bills found for " + inputDate);
    			return;
		}
		
		// --- Count files --- 
	
		File[] jsonBills = dateFolder.listFiles((dir, name) -> name.endsWith(".json"));

		int jsonCount = 0;  // <-- count actual number of individual bills

		double totalRevenue = 0.0;
		double allDiscount = 0.0;
	
		if (jsonBills != null) {
    			Gson gson = new Gson();
    			for (File bill : jsonBills) {
        			try (Reader reader = new FileReader(bill)) {
            				JsonObject json = gson.fromJson(reader, JsonObject.class);

            				if (json.has("bills")) {
                				JsonArray billsArray = json.getAsJsonArray("bills");

               				 	// Count number of bills
               				 	jsonCount += billsArray.size();

               				 	// Sum finalBill from each bill
                				for (JsonElement billElement : billsArray) {
                   					JsonObject billObject = billElement.getAsJsonObject();
                    					if (billObject.has("finalBill")) {
                        					totalRevenue += billObject.get("finalBill").getAsDouble();
                    					}
									
							if (billObject.has("discount")) {
                        					allDiscount += billObject.get("discount").getAsDouble();
                    					}

                				}
           			 	}

        			} catch (IOException | JsonSyntaxException e) {
           			 	System.out.println("Error reading " + bill.getName() + ": " + e.getMessage());
        			}
    			}
		}

		System.out.println("DEBUG: Found " + jsonCount + " bills with total revenue: Rs. " + totalRevenue);


		// --- Print summary ---
		String summary = String.format(
   			 "\n Bill Summary for %s:\n" +
    			" total bills: %d\n" +
    			" Total revenue for all bills (finalBill): Rs. %.2f\n" +
			" total discounts for all bills : %.2f\n",
    			inputDate, jsonCount, totalRevenue,allDiscount
		);

		System.out.println(summary);

		// Save summary as JSON
		JsonObject summaryJson = new JsonObject();
		summaryJson.addProperty("date", inputDate);
		summaryJson.addProperty("totalBills", jsonCount);
		summaryJson.addProperty("allDiscounts", allDiscount);
		summaryJson.addProperty("totalRevenue", totalRevenue);

		// Define summary file path
		 File summaryFile = new File(folderPath, "summary_" + inputDate + ".json");

		// Write JSON to file
		try (Writer writer = new FileWriter(summaryFile)) {
    			Gson gson = new GsonBuilder().setPrettyPrinting().create();
   			gson.toJson(summaryJson, writer);
    			System.out.println(" JSON summary saved to: " + summaryFile.getAbsolutePath());
		} catch (IOException e) {
    			System.out.println(" Failed to save JSON summary: " + e.getMessage());
		} 
		
	
		// ended new
		System.out.println("are you new Customer.? Or existing customer");
		System.out.println();
		System.out.println("type 'new' OR 'existing'..");
		
		String customerType = scanner.nextLine();
	
		System.out.println("please enter your mobile number");
		String mobileNumber = scanner.nextLine();
		
		if(mobileNumber.length() != 10) {
			System.out.println("invalid mobile number. please enter correct one.");
			return;
		}

		else {
			System.out.println("mobile number saved successfully.");
			System.out.println();
		}

		LocalDateTime now = LocalDateTime.now();
		String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		
		//	String folderPath = "Bills/" + year + "/" + month + "/" + day;
			
		File BillFolder = new File(folderPath); 
		
		if (!BillFolder.exists()) {
    			BillFolder.mkdirs(); 
    			System.out.println("Created folder path: " + folderPath);
		}  

		String fileName = mobileNumber + "_" + time + ".txt";
		File billFile = new File(BillFolder, fileName);

		File previousBill = new File(fileName);	
		
		if (customerType.equalsIgnoreCase("existing")) {
		
			System.out.println("do you want see your previous bills.? ( yes or no) ");
			String response = scanner.nextLine();
		
			if(response.equalsIgnoreCase("yes")) {
				File billsRoot = new File("Bills");
   		
				List<File> allBills = findAllBillsByMobile(billsRoot, mobileNumber);

				if (!allBills.isEmpty()) {
    					System.out.println("\nYour previous bills:\n");

					for (File bill : allBills) {
        					System.out.println(" ----- " + bill.getName() + " ----- ");

        					try (BufferedReader br = new BufferedReader(new FileReader(bill))) {
            						String line;
            						while ((line = br.readLine()) != null) {
               	 						System.out.println(line);
            						}
        					} catch (IOException e) {
            						System.out.println("Error reading bill: " + bill.getName());
       		 				}

        					System.out.println(); // Add a blank line between bills
   					}
						
					List<File> allJsons = findAllJsonFilesByMobile(billsRoot, mobileNumber);

					double totalCustomerRevenue = 0.0; // <-- For summing finalBill

					if (!allJsons.isEmpty()) {
  						System.out.println("\n Your JSON bill history:\n");

    						allJsons.sort(Comparator.comparingLong(File::lastModified)); // Optional: sort by date

    						Gson gson = new GsonBuilder().setPrettyPrinting().create();

    						for (File json : allJsons) {
        						System.out.println("----- " + json.getName() + " -----");

        						try (Reader reader = new FileReader(json)) {
            							JsonElement jsonElement = JsonParser.parseReader(reader);
            							JsonObject jsonObj = jsonElement.getAsJsonObject();

           							if (jsonObj.has("bills")) {
                							JsonArray billsArray = jsonObj.getAsJsonArray("bills");

                							// Sum all finalBill values
                						
									for (JsonElement billElement : billsArray) {
                   								JsonObject billObject = billElement.getAsJsonObject();
                    								if (billObject.has("finalBill")) {
                        						   		totalCustomerRevenue += billObject.get("finalBill").getAsDouble();
                    							
										}
                							}

               								// Pretty-print the JSON
                							String prettyJson = gson.toJson(jsonObj);
                							System.out.println(prettyJson);
            							} else {
               							 	System.out.println("No 'bills' found in this JSON.");
            							}

        						} catch (IOException | JsonSyntaxException e) {
            							System.out.println(" Error reading JSON file: " + json.getName());
        						}

        						System.out.println(); // blank line between files
    						}

    						System.out.printf(" Total Revenue from all bills: Rs. %.2f\n", totalCustomerRevenue);
					} else {
   						 System.out.println(" No JSON bills found for this mobile number.");
					}


				}

    				System.out.println("Do you want to continue shopping and add more items? (yes OR no)");
    				String input = scanner.nextLine();
   				
				if (!input.equalsIgnoreCase("yes")) {
        				System.out.println("Thank you for visiting again!");
        				return;
   				}
			} else {
   				 System.out.println("\nNo previous bills found for this mobile number.");
   				 System.out.println("Starting a new bill...");
			} 
		} 
	
		else {
  			System.out.println("Welcome, new customer!");
		}	
	
		System.out.println("-----------------------");
		System.out.println("Available Items list : ");
		System.out.println();
		
		Map<String,Double> Items = new TreeMap<>();
		Items.put("Apple", 50.0);
		Items.put("Orange", 20.0);  

		Items.put("Papaya", 40.0);
		Items.put("Kiwi", 20.0);
		Items.put("Banana" , 10.0);
		Items.put("Grapes" , 45.0);

	       	 List<String> itemList = new ArrayList<>(Items.keySet());
		LinkedHashMap<String,Integer> cart = new LinkedHashMap<>();
							
		int i = 1;
		for(Map.Entry<String,Double> entry : Items.entrySet()) {
			System.out.println(i + "." +  entry.getKey() + " " + entry.getValue());
			i++;
		}
		System.out.println();

		double totalBill = 0.0;
		double finalBill = 0.0;
		int selectCount = 0;

 		while(true) {
			System.out.println("Select  Items from the list.. (Or) enter '0' to view your cart.");
			try {
				int number = scanner.nextInt();
				scanner.nextLine();
			
				if(number == 0) {
					System.out.println();
					System.out.println("your current Cart Items ");
					System.out.println();
					System.out.printf("%-5s %-10s %-10s %-10s %-10s\n","ID", "Item", "price", "quantity" , "SubTotal");
	
					for(Map.Entry<String,Integer> entry : cart.entrySet()) {
						String Item = entry.getKey();
						int quantity = entry.getValue();
						int itemId = itemList.indexOf(Item) + 1;
						double price = Items.get(Item);
						double SubTotal = price * quantity;

						System.out.printf("%-5d %-10s Rs.%-9.2f %-10d Rs.%-9.2f\n",itemId, Item , price, quantity  , SubTotal);	
					}
					
					System.out.println();
					System.out.println(" Your total amount is : " + totalBill);
					
					System.out.println("   -------------------------------  ");
			
					System.out.println(" Do you want to add any items..? or Remove items.?");
					System.out.println();
					System.out.println("enter 'add' to add items into the cart.");
					System.out.println("		OR");
					System.out.println("enter 'remove' to remove items from the cart.");
					System.out.println("		OR");
					System.out.println("enter 'exit' to exit..");
					System.out.println();
		
					String input = scanner.nextLine();
			       		
				 	if(input.equalsIgnoreCase("exit")) {
						System.out.println();
			 			System.out.println("Calculating Bill..");
						 break;
						//continue;		
					}
				
					else if(input.equalsIgnoreCase("add")) {
						System.out.println("you can continue your shopping.\n");
						continue;
					}
			
					while(true) {
						if (input.equalsIgnoreCase("remove")) {
							while(true) {
								System.out.println("plese enter product Id. which you want to remove.");
					
								int removedNumber = scanner.nextInt();
								scanner.nextLine();
								
								if(removedNumber >= 1 && removedNumber <= itemList.size()) {
									String itemToRemove = itemList.get(removedNumber - 1);
						
									if(cart.containsKey(itemToRemove)) {
										int currentQty = cart.get(itemToRemove);
										System.out.println("current quantity of " + itemToRemove + " in your cart is :" + currentQty);
										System.out.println("Enter quantityToRemove");
										int QuantityToRemove = scanner.nextInt();
												
										scanner.nextLine();
			
										if(QuantityToRemove <= 0) {
											System.out.println("please enter correct quantity. it must be greater than 0 ");
										}		
										else if(QuantityToRemove >= currentQty) {
											cart.remove(itemToRemove);
											System.out.println();
											System.out.println(itemToRemove + " removed from  the cart");
										}	
										else {
                									cart.put(itemToRemove, currentQty - QuantityToRemove);
         						      				System.out.println(QuantityToRemove + " units of " + itemToRemove + " removed.");
										}
						 					totalBill = calculateTotalBill(cart, Items);
									}
					
									else {
										System.out.println("Item is not in the cart.");
										continue;
									}
									break;
								}
						
					
								else {
									System.out.println("invalid input.! please enter correct one.");	
									continue;
								}
							}
							System.out.println();
								//	break;
						}

						else {
							System.out.println("please enter valid input.");
							break;						
						}
					}
				}					
				
				if(number >= 1 && number <= itemList.size()) {
					String SelectedItem = itemList.get(number - 1);
					Double price = Items.get(SelectedItem);
					
					System.out.println();
					System.out.println("you selected " + SelectedItem + ", Available for " + price + " rupees");
					System.out.println();
	
					int counting = 0;
					while(true) {
						try {
							System.out.println("enter quantity");
							int quantity = scanner.nextInt();
							scanner.nextLine();
								
							if(quantity > 0) {
								double SubTotal = price * quantity;
								totalBill += SubTotal;
								
								cart.put(SelectedItem , cart.getOrDefault(SelectedItem,0) + quantity );
								System.out.println();
								
								System.out.println("Cost of " + SelectedItem + " " + quantity + " * " + price + " = " + SubTotal);
									
								totalBill = calculateTotalBill(cart, Items);
								System.out.println();
								break;
							}
							else {
								System.out.println("please enter correct quantity. it must be greater than 0.");
							}
						}
						catch(InputMismatchException e) {
							System.out.println("invalid quantity.! please enter quantity in numeric value..");
							System.out.println();
							scanner.nextLine();
							// continue;
							counting++;
	
							if(counting > 3) {
								System.out.println("Too many invalid attemps...");
								System.out.println();
								break;
							}		
						}
					}			
				}

				else {
					System.out.println("Invalid selection, Try again, please select only available items from the list.");
					System.out.println();
				}
			}

			catch(InputMismatchException e) {
				System.out.println("invalid Selection.! please enter correct one.");
				System.out.println();
				selectCount++;
				System.out.println();
				scanner.nextLine();

				if(selectCount >= 3) {
					System.out.println("Too many invalid attempts..");
					break;
				}
			}
		}
			
		System.out.println(" final Cart Items -- ");
		System.out.println();

		System.out.printf("%-5s %-10s %-10s %-10s %-10s\n","ID", "Item", "price", "quantity" , "SubTotal");
	
		for(Map.Entry<String,Integer> entry : cart.entrySet()) {
			String Item = entry.getKey();
			int quantity = entry.getValue();
			int itemId = itemList.indexOf(Item) + 1;
			double price = Items.get(Item);
			double SubTotal = price * quantity;

			System.out.printf("%-5d %-10s Rs.%-9.2f %-10d Rs.%-7.2f\n",itemId, Item , price, quantity  , SubTotal);		
		}

		System.out.println("	------------------------------	  ");

		System.out.println(" your current bill is : " + totalBill + " rupees. ");

		System.out.println();
		double discountPercentage = 0.0;
		int count = 0;

		while(true) {
			try {
		   		System.out.println("enter discount percentage (Max 15%) to apply. ");
	
				double percentage = scanner.nextDouble();
					
				System.out.println();
				scanner.nextLine();
					
				if(percentage > 15.0) {
			    
					System.out.println("invalid discount.! please enter discount below 15%.");
					System.out.println();
					count++;

					if(count > 3) {
						System.out.println("too many attempts.. No discounts applied. ");
						System.out.println();
						System.out.println("your final Bill is : " + totalBill);
						break;
					}
					continue;	
				}
	
				else {
					System.out.println("Applied discount.." + percentage + "%");
					System.out.println();
					discountPercentage = (percentage / 100) * totalBill;
			
					finalBill = totalBill - discountPercentage;
					
					System.out.println("your discount amount is " + discountPercentage + " rupees."); 
					System.out.println();
					System.out.println("After discount your total Bill is : ");
					System.out.println();	
					System.out.println("finalBill " + finalBill + " Rupees.");	
					System.out.println();
					break;
				}
			}
			
			catch(InputMismatchException e) {
				System.out.println("invalid input.! please enter discount in numeric values.");
				System.out.println();
				scanner.nextLine();
				count++;

				if(count > 3) {
					System.out.println("too many invalid attempts.. No discounts applied.");
					System.out.println();
					System.out.println("your final Bill is : " + totalBill);
					break;
				}
			}  	
		}
		
		System.out.println();
		System.out.println("THANK YOU FOR VISITING OUR STORE..");
		
		try {
			FileWriter fw = new FileWriter(billFile);
		 	fw.write("\n-----Blinkit store Bill-----\n\n");
			fw.write(String.format("%-5s %-10s %-10s %-10s %-10s\n","ID", "Item", "price", "quantity" , "SubTotal"));

			for(Map.Entry<String,Integer> entry : cart.entrySet()) {
				String Item = entry.getKey();
				int quantity = entry.getValue();
				int itemId = itemList.indexOf(Item) + 1;
				double price = Items.get(Item);
				double SubTotal = price * quantity;

				fw.write(String.format("%-5d %-10s Rs.%-9.2f %-10d Rs.%-7.2f\n",itemId, Item , price, quantity  , SubTotal));	
			}
			
			fw.write(" ----------------------- ");
			fw.write(" ----------------------- ");

			fw.write ("\n-------------------------------\n");
           		fw.write (String.format("Original Bill: %.2f\n", totalBill));
           		fw.write (String.format("Discount: %.2f\n", discountPercentage));
           		fw.write (String.format("Final Bill: %.2f\n", finalBill));
           		fw.write ("\n===============================\n");
          		fw.write ("Thank you for shopping with us!\n");

		 } catch (IOException e) {
           		System.err.println("Error writing bill file: " + e.getMessage());
            		e.printStackTrace();
      		 }
		
		try {
			List<Map<String, Object>> cartList = new ArrayList<>();
    			 for (Map.Entry<String, Integer> entry : cart.entrySet()) {
       			 	String item = entry.getKey();
       				int quantity = entry.getValue();
 			       	 double price = Items.get(item);
        			double subtotal = price * quantity;

			       	Map<String, Object> itemJson = new LinkedHashMap<>();
       				itemJson.put("id", itemList.indexOf(item) + 1);
        			itemJson.put("name", item);
        			itemJson.put("price", price);
        			itemJson.put("quantity", quantity);
        			itemJson.put("subtotal", subtotal);

        			cartList.add(itemJson);
    			} 

			String jsonFilename = mobileNumber + ".json";  
    			File jsonFile = new File(BillFolder, jsonFilename);
    			Gson gson = new GsonBuilder().setPrettyPrinting().create();

   			JsonObject rootJson;

    			if (jsonFile.exists()) {
        			// Read existing file
        			try (Reader reader = new FileReader(jsonFile)) {
           				rootJson = JsonParser.parseReader(reader).getAsJsonObject();
        			}
   			} else {
       				 // New file
        			rootJson = new JsonObject();
					
				rootJson.addProperty("mobile", mobileNumber);
					
        			rootJson.add("bills", new JsonArray());
			}
			if (!rootJson.has("bills") || !rootJson.get("bills").isJsonArray()) {
        			rootJson.add("bills", new JsonArray());
  			 }

    			// Build current bill
    			JsonObject newBill = new JsonObject();
    			newBill.addProperty("timestamp", time);
    			newBill.addProperty("originalBill", totalBill);
    			newBill.addProperty("discount", discountPercentage);
    			newBill.addProperty("finalBill", finalBill);

    			JsonArray cartArray = new JsonArray();
    			for (Map.Entry<String, Integer> entry : cart.entrySet()) {
        			String item = entry.getKey();
        			int quantity = entry.getValue();
        			double price = Items.get(item);
        			double subtotal = price * quantity;

        			JsonObject itemJson = new JsonObject();
        			itemJson.addProperty("id", itemList.indexOf(item) + 1);
        			itemJson.addProperty("name", item);
        			itemJson.addProperty("price", price);
        			itemJson.addProperty("quantity", quantity);
        			itemJson.addProperty("subtotal", subtotal);

        			cartArray.add(itemJson);
		
    			}

    			newBill.add("cart", cartArray);
	
			rootJson.getAsJsonArray("bills").add(newBill);
								
			//  Recalculate totalRevenue from all finalBill values
					
			double recalculatedRevenue = 0.0;
			double totalDiscounts = 0.0;
			double totalQuantities = 0.0;
				
			JsonArray billsArray = rootJson.getAsJsonArray("bills");
				
			//	double recalculatedRevenue = 0.0;

			for (JsonElement billElement : billsArray) {
    				JsonObject billObject = billElement.getAsJsonObject();

    				if (billObject.has("finalBill")) {
        				recalculatedRevenue += billObject.get("finalBill").getAsDouble();	
   				 }

				if(billObject.has("discount")) {
					totalDiscounts += billObject.get("discount").getAsDouble();
				}
							
				 if (billObject.has("cart")) {
        				JsonArray cartItems = billObject.getAsJsonArray("cart");
       					 for (JsonElement itemElement : cartItems) {
           					 JsonObject item = itemElement.getAsJsonObject();
            					if (item.has("quantity")) {
                					totalQuantities += item.get("quantity").getAsInt();
           					 }
        				}
   				}
			}

			rootJson.addProperty("totalRevenue", recalculatedRevenue);
			rootJson.addProperty("totalDiscounts", totalDiscounts);
			rootJson.addProperty("totalQuantities", totalQuantities);

			//  Write updated JSON back to file
				
			try (Writer writer = new FileWriter(jsonFile)) {
				 gson.toJson(rootJson, writer);
		
				System.out.println("Consolidated JSON bill saved as: " + jsonFilename);


			} catch (IOException e) {
				System.out.println("Failed to save JSON bill: " + e.getMessage());
			}

		} catch (IOException | JsonParseException e) {
   			System.out.println("Failed to save JSON bill: " + e.getMessage());
		} 
			
		  scanner.close();
		
	}
			public static File findLatestBillByMobile(File folder, String mobileNumber) {
   				File latest = null;

   		 		File[] files = folder.listFiles();
    				if (files != null) {
        			for (File file : files) {
            				if (file.isDirectory()) {
                				File subLatest = findLatestBillByMobile(file, mobileNumber);
               					if (subLatest != null) {
                  					if (latest == null || subLatest.lastModified() > latest.lastModified()) {
                       						latest = subLatest;
                   				 	}
                				}
            				}
						else if (file.getName().equals(mobileNumber + ".json")) {
                				if (latest == null || file.lastModified() > latest.lastModified()) {
                   					 latest = file;
              					}
          				}
       				}
  			}
    			return latest;
		}

		public static List<File> findAllBillsByMobile(File folder, String mobileNumber) {
  			List<File> matchingBills = new ArrayList<>();

    			File[] files = folder.listFiles();
    			if (files != null) {
        			for (File file : files) {
            				if (file.isDirectory()) {
                				matchingBills.addAll(findAllBillsByMobile(file, mobileNumber));
            				} 
                				else if (file.getName().equals(mobileNumber + ".json")) {

							matchingBills.add(file);
            				}
        			}
    			}

   	 	return matchingBills;
	}	

	// new one

	public static List<File> findAllJsonFilesByMobile(File folder, String mobileNumber) {
    		List<File> jsonFiles = new ArrayList<>();

          	File[] files = folder.listFiles();
  	 	if (files != null) {
       	  		for (File file : files) {
      	     			if (file.isDirectory()) {
                			jsonFiles.addAll(findAllJsonFilesByMobile(file, mobileNumber));
            			} 
                				
					else if (file.getName().equals(mobileNumber + ".json")) {

					jsonFiles.add(file);
            			}
        		}
    		}
	 return jsonFiles;
	}
}

