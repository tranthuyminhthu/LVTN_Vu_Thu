import pandas as pd
import matplotlib.pyplot as plt
import os
import numpy as np
from collections import Counter

# Đường dẫn tới dataset và file CSV
data_dir = '/kaggle/input/deepfashion2-original-with-dataframes/DeepFashion2/img_info_dataframes'
train_csv = os.path.join(data_dir, 'train.csv')
validation_csv = os.path.join(data_dir, 'validation.csv')

# Đọc dữ liệu từ file CSV
print("Loading data...")
train_df = pd.read_csv(train_csv)
val_df = pd.read_csv(validation_csv)

print(f"Original train dataset size: {len(train_df)}")
print(f"Original validation dataset size: {len(val_df)}")

# Tạo mapping từ category_id sang category_name từ dataset
print("\n=== CREATING CATEGORY MAPPING FROM DATASET ===")
category_mapping_from_dataset = {}
for _, row in train_df.iterrows():
    cat_id = row['category_id']
    cat_name = row['category_name']
    if cat_id not in category_mapping_from_dataset:
        category_mapping_from_dataset[cat_id] = cat_name
        print(f"  Category {cat_id}: {cat_name}")

print(f"\nTotal unique categories found: {len(category_mapping_from_dataset)}")

# Phân tích phân bố category ban đầu
print("\n=== ORIGINAL CATEGORY DISTRIBUTION ===")
original_category_counts = train_df['category_id'].value_counts()
print(f"Total categories: {len(original_category_counts)}")
print(f"Categories with samples:")
for category_id, count in original_category_counts.items():
    category_name = category_mapping_from_dataset.get(category_id, f"Unknown Category {category_id}")
    print(f"  Category {category_id} ({category_name}): {count:,} samples")

# Lọc dataset theo threshold
min_samples_threshold = 10000
print(f"\n=== FILTERING WITH THRESHOLD: {min_samples_threshold:,} SAMPLES ===")

category_counts = train_df['category_id'].value_counts()
valid_categories = category_counts[category_counts >= min_samples_threshold].index
filtered_categories = category_counts[category_counts < min_samples_threshold].index

print(f"Categories kept: {len(valid_categories)}")
print(f"Categories filtered out: {len(filtered_categories)}")

# Hiển thị categories bị loại bỏ
print("\nCategories filtered out:")
for cat_id in filtered_categories:
    count = category_counts[cat_id]
    category_name = category_mapping_from_dataset.get(cat_id, f"Unknown Category {cat_id}")
    print(f"  Category {cat_id} ({category_name}): {count:,} samples")

# Lọc dataset
train_df_filtered = train_df[train_df['category_id'].isin(valid_categories)].copy()
val_df_filtered = val_df[val_df['category_id'].isin(valid_categories)].copy()

print(f"\nFiltered train dataset size: {len(train_df_filtered)}")
print(f"Filtered validation dataset size: {len(val_df_filtered)}")

# Cập nhật category_id mapping
category_mapping = {old_id: new_id for new_id, old_id in enumerate(sorted(valid_categories))}
train_df_filtered['category_id'] = train_df_filtered['category_id'].map(category_mapping)
val_df_filtered['category_id'] = val_df_filtered['category_id'].map(category_mapping)

# Hiển thị mapping
print("\n=== CATEGORY MAPPING ===")
for old_id, new_id in category_mapping.items():
    count = category_counts[old_id]
    category_name = category_mapping_from_dataset.get(old_id, f"Unknown Category {old_id}")
    print(f"  Original Category {old_id} ({category_name}) -> New Category {new_id}: {count:,} samples")

# Vẽ biểu đồ phân bổ category
def plot_category_distribution(category_counts, valid_categories, category_mapping, output_file='category_distribution.png'):
    # Chuẩn bị dữ liệu cho biểu đồ
    categories = []
    counts = []
    for cat in valid_categories:
        old_id = cat
        new_id = category_mapping[cat]
        category_name = category_mapping_from_dataset.get(old_id, f"Unknown Category {old_id}")
        categories.append(f"Category {new_id}\n({category_name})")
        counts.append(category_counts[cat])
    
    # Tạo biểu đồ cột
    plt.figure(figsize=(14, 10))
    
    # Tạo subplot cho biểu đồ cột
    plt.subplot(2, 1, 1)
    colors = ['#4e79a7', '#f28e2b', '#e15759', '#76b7b2', '#59a14f', '#edc948', '#b07aa1', '#ff9da7', '#9c755f']
    bars = plt.bar(categories, counts, color=colors[:len(categories)], edgecolor='black')
    
    # Thêm số liệu trên mỗi cột
    for bar, count in zip(bars, counts):
        plt.text(bar.get_x() + bar.get_width()/2, bar.get_height() + max(counts)*0.01, 
                f'{count:,}', ha='center', va='bottom', fontweight='bold')
    
    # Tùy chỉnh biểu đồ
    plt.xlabel('Category')
    plt.ylabel('Number of Samples')
    plt.title('Distribution of Categories in DeepFashion2 Dataset (After Filtering)')
    plt.xticks(rotation=45, ha='right')
    plt.grid(True, axis='y', linestyle='--', alpha=0.7)
    
    # Tạo subplot cho biểu đồ tròn
    plt.subplot(2, 1, 2)
    plt.pie(counts, labels=categories, autopct='%1.1f%%', startangle=90)
    plt.title('Percentage Distribution of Categories')
    
    # Lưu và hiển thị biểu đồ
    plt.tight_layout()
    plt.savefig(output_file, dpi=300, bbox_inches='tight')
    plt.show()
    plt.close()

# Vẽ biểu đồ so sánh trước và sau khi lọc
def plot_comparison_before_after(original_counts, filtered_counts, valid_categories, output_file='category_comparison.png'):
    plt.figure(figsize=(18, 10))
    
    # Biểu đồ trước khi lọc
    plt.subplot(1, 2, 1)
    all_categories = sorted(original_counts.index)
    original_data = [original_counts[cat] for cat in all_categories]
    colors = ['red' if cat not in valid_categories else 'green' for cat in all_categories]
    
    # Tạo labels với tên category
    labels = []
    for cat in all_categories:
        category_name = category_mapping_from_dataset.get(cat, f"Unknown {cat}")
        labels.append(f"Cat {cat}\n({category_name})")
    
    bars1 = plt.bar(range(len(all_categories)), original_data, color=colors, alpha=0.7)
    plt.xlabel('Original Category ID')
    plt.ylabel('Number of Samples')
    plt.title('Original Category Distribution\n(Red = Filtered out, Green = Kept)')
    plt.xticks(range(len(all_categories)), labels, rotation=45, ha='right')
    plt.grid(True, axis='y', alpha=0.3)
    
    # Thêm threshold line
    plt.axhline(y=min_samples_threshold, color='orange', linestyle='--', label=f'Threshold ({min_samples_threshold:,})')
    plt.legend()
    
    # Biểu đồ sau khi lọc
    plt.subplot(1, 2, 2)
    filtered_categories_list = []
    filtered_data = []
    for cat in valid_categories:
        new_id = category_mapping[cat]
        category_name = category_mapping_from_dataset.get(cat, f"Unknown {cat}")
        filtered_categories_list.append(f"Cat {new_id}\n({category_name})")
        filtered_data.append(filtered_counts[cat])
    
    bars2 = plt.bar(filtered_categories_list, filtered_data, color='green', alpha=0.7)
    plt.xlabel('New Category ID')
    plt.ylabel('Number of Samples')
    plt.title('Filtered Category Distribution')
    plt.xticks(rotation=45, ha='right')
    plt.grid(True, axis='y', alpha=0.3)
    
    # Thêm số liệu trên mỗi cột
    for bar, count in zip(bars2, filtered_data):
        plt.text(bar.get_x() + bar.get_width()/2, bar.get_height() + max(filtered_data)*0.01, 
                f'{count:,}', ha='center', va='bottom', fontweight='bold')
    
    plt.tight_layout()
    plt.savefig(output_file, dpi=300, bbox_inches='tight')
    plt.show()
    plt.close()

# Vẽ biểu đồ thống kê tổng quan
def plot_statistics_summary(original_counts, filtered_counts, valid_categories, output_file='statistics_summary.png'):
    # Tính toán thống kê
    total_original = original_counts.sum()
    total_filtered = sum(filtered_counts[cat] for cat in valid_categories)
    removed_samples = total_original - total_filtered
    kept_percentage = (total_filtered / total_original) * 100
    
    # Tạo biểu đồ
    fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(15, 12))
    
    # 1. Tổng số samples
    ax1.bar(['Original', 'Filtered'], [total_original, total_filtered], 
            color=['lightblue', 'lightgreen'], edgecolor='black')
    ax1.set_title('Total Samples')
    ax1.set_ylabel('Number of Samples')
    for i, v in enumerate([total_original, total_filtered]):
        ax1.text(i, v + max(total_original, total_filtered)*0.01, f'{v:,}', ha='center', va='bottom', fontweight='bold')
    
    # 2. Số categories
    ax2.bar(['Original', 'Filtered'], [len(original_counts), len(valid_categories)], 
            color=['lightcoral', 'lightgreen'], edgecolor='black')
    ax2.set_title('Number of Categories')
    ax2.set_ylabel('Number of Categories')
    for i, v in enumerate([len(original_counts), len(valid_categories)]):
        ax2.text(i, v + 0.1, str(v), ha='center', va='bottom', fontweight='bold')
    
    # 3. Samples bị loại bỏ
    ax3.pie([total_filtered, removed_samples], labels=['Kept', 'Removed'], 
            autopct='%1.1f%%', colors=['lightgreen', 'lightcoral'])
    ax3.set_title('Sample Distribution')
    
    # 4. Thống kê chi tiết
    stats_text = f"""
    Original Dataset:
    - Total samples: {total_original:,}
    - Categories: {len(original_counts)}
    
    After Filtering:
    - Kept samples: {total_filtered:,}
    - Removed samples: {removed_samples:,}
    - Kept categories: {len(valid_categories)}
    - Kept percentage: {kept_percentage:.1f}%
    
    Threshold: {min_samples_threshold:,} samples
    """
    ax4.text(0.1, 0.5, stats_text, transform=ax4.transAxes, fontsize=12, 
             verticalalignment='center', bbox=dict(boxstyle="round,pad=0.3", facecolor="lightgray"))
    ax4.set_title('Detailed Statistics')
    ax4.axis('off')
    
    plt.tight_layout()
    plt.savefig(output_file, dpi=300, bbox_inches='tight')
    plt.show()
    plt.close()

# Gọi các hàm vẽ biểu đồ
print("\n=== GENERATING PLOTS ===")

# 1. Biểu đồ phân bố category sau khi lọc
plot_category_distribution(category_counts, valid_categories, category_mapping, 'category_distribution.png')
print("✓ Generated category_distribution.png")

# 2. Biểu đồ so sánh trước và sau khi lọc
plot_comparison_before_after(original_category_counts, category_counts, valid_categories, 'category_comparison.png')
print("✓ Generated category_comparison.png")

# 3. Biểu đồ thống kê tổng quan
plot_statistics_summary(original_category_counts, category_counts, valid_categories, 'statistics_summary.png')
print("✓ Generated statistics_summary.png")

# In thống kê cuối cùng
print("\n=== FINAL STATISTICS ===")
print(f"Original dataset: {len(train_df):,} samples, {len(original_category_counts)} categories")
print(f"Filtered dataset: {len(train_df_filtered):,} samples, {len(valid_categories)} categories")
print(f"Removed: {len(train_df) - len(train_df_filtered):,} samples ({len(original_category_counts) - len(valid_categories)} categories)")
print(f"Kept percentage: {(len(train_df_filtered) / len(train_df)) * 100:.1f}%")

print("\n=== CATEGORY MAPPING SUMMARY ===")
for old_id, new_id in category_mapping.items():
    count = category_counts[old_id]
    category_name = category_mapping_from_dataset.get(old_id, f"Unknown Category {old_id}")
    print(f"Category {old_id} ({category_name}) -> Category {new_id}: {count:,} samples")

print("\n=== CATEGORIES KEPT ===")
for old_id in sorted(valid_categories):
    new_id = category_mapping[old_id]
    count = category_counts[old_id]
    category_name = category_mapping_from_dataset.get(old_id, f"Unknown Category {old_id}")
    print(f"Category {new_id}: {category_name} ({count:,} samples)")

print("\n=== CATEGORIES REMOVED ===")
for old_id in sorted(filtered_categories):
    count = category_counts[old_id]
    category_name = category_mapping_from_dataset.get(old_id, f"Unknown Category {old_id}")
    print(f"Category {old_id}: {category_name} ({count:,} samples)")

print("\nAnalysis completed! Check the generated PNG files for visualizations.") 