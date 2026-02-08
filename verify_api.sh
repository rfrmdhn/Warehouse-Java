#!/bin/bash
BASE_URL="http://localhost:8080/api"

echo "---------------------------------------------------"
echo "🚀 LIVE API VERIFICATION (CRUD & LOGIC)"
echo "---------------------------------------------------"

# 1. CREATE ITEM
echo "\n[1] Creating Item 'Gaming Laptop'..."
response=$(curl -s -X POST $BASE_URL/items \
  -H "Content-Type: application/json" \
  -d '{"name": "Gaming Laptop", "description": "High Performance"}')
echo "Response: $response"

# Extract Item ID (Simple grep hack)
ITEM_ID=$(echo $response | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo ">> Extracted Item ID: $ITEM_ID"

if [ -z "$ITEM_ID" ]; then
  echo "❌ Failed to create item. Exiting."
  exit 1
fi

# 2. ADD VARIANT
echo "\n[2] Adding Variant 'RTX 4060' (Stock: 10)..."
response=$(curl -s -X POST $BASE_URL/items/$ITEM_ID/variants \
  -H "Content-Type: application/json" \
  -d '{"name": "RTX 4060", "price": 1500, "stockQuantity": 10}')
echo "Response: $response"

VARIANT_ID=$(echo $response | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo ">> Extracted Variant ID: $VARIANT_ID"

# 3. VERIFY PERSISTENCE (READ)
echo "\n[3] Verifying Data Persistence (GET)..."
check=$(curl -s $BASE_URL/items/$ITEM_ID)
echo "DB Content: $check"
if echo "$check" | grep -q "Gaming Laptop"; then
  echo "✅ Item persisted in Database."
else
  echo "❌ Item NOT found in Database."
fi

# 4. SELL ITEM (UPDATE)
echo "\n[4] Selling 2 units..."
curl -s -X POST $BASE_URL/orders \
  -H "Content-Type: application/json" \
  -d "{\"variantId\": $VARIANT_ID, \"quantity\": 2}"
echo " (Sold)"

# 5. VERIFY STOCK UPDATE
echo "\n[5] Verifying Stock Update (Expected: 8)..."
stock_check=$(curl -s $BASE_URL/items/$ITEM_ID)
# We look for stockQuantity:8 in the extracted JSON
if echo "$stock_check" | grep -q '"stockQuantity":8'; then
  echo "✅ Stock updated correctly to 8 in Database."
else
  echo "❌ Stock update FAILED. Content: $stock_check"
fi

# 6. ATTEMPT OVERSELLING (LOGIC)
echo "\n[6] Attempting to sell 9 units (Should FAIL)..."
http_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST $BASE_URL/orders \
  -H "Content-Type: application/json" \
  -d "{\"variantId\": $VARIANT_ID, \"quantity\": 9}")

if [ "$http_code" == "400" ]; then
  echo "✅ Overselling prevented (HTTP 400)."
else
  echo "❌ Logic Error: Overselling allowed or wrong error (HTTP $http_code)."
fi

echo "\n---------------------------------------------------"
echo "✅ Verification Complete"
echo "---------------------------------------------------"
