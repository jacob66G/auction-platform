import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CategoryResponse, CategoryRequest } from '../../../models/category.model';
import { CategoryService } from '../../../services/category.service';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ]
})
export class CategoryListComponent implements OnInit {
  categories: CategoryResponse[] = [];
  categoryForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  isEditing = false;
  editingCategoryId: number | null = null;

  constructor(
    private categoryService: CategoryService,
    private formBuilder: FormBuilder
  ) {
    this.categoryForm = this.formBuilder.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]]
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getCategories().subscribe(
      data => {
        this.categories = data;
        this.isLoading = false;
      },
      error => {
        console.error('Error loading categories:', error);
        this.errorMessage = 'Nie udało się załadować kategorii.';
        this.isLoading = false;
      }
    );
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const categoryRequest: CategoryRequest = {
      name: this.categoryForm.value.name
    };

    if (this.isEditing && this.editingCategoryId) {
      // Update existing category
      this.categoryService.updateCategory(this.editingCategoryId, categoryRequest).subscribe(
        response => {
          this.successMessage = 'Kategoria została zaktualizowana.';
          this.loadCategories();
          this.resetForm();
        },
        error => {
          console.error('Error updating category:', error);
          this.errorMessage = 'Nie udało się zaktualizować kategorii.';
          this.isLoading = false;
        }
      );
    } else {
      // Create new category
      this.categoryService.createCategory(categoryRequest).subscribe(
        response => {
          this.successMessage = 'Kategoria została dodana.';
          this.loadCategories();
          this.resetForm();
        },
        error => {
          console.error('Error creating category:', error);
          this.errorMessage = 'Nie udało się utworzyć kategorii.';
          this.isLoading = false;
        }
      );
    }
  }

  editCategory(category: CategoryResponse): void {
    this.isEditing = true;
    this.editingCategoryId = category.id;
    this.categoryForm.setValue({
      name: category.name
    });
  }

  deleteCategory(id: number): void {
    if (confirm('Czy na pewno chcesz usunąć tę kategorię? Może to wpłynąć na istniejące aukcje.')) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';

      this.categoryService.deleteCategory(id).subscribe(
        () => {
          this.successMessage = 'Kategoria została usunięta.';
          this.loadCategories();
        },
        error => {
          console.error('Error deleting category:', error);
          this.errorMessage = 'Nie udało się usunąć kategorii.';
          this.isLoading = false;
        }
      );
    }
  }

  resetForm(): void {
    this.categoryForm.reset();
    this.isEditing = false;
    this.editingCategoryId = null;
    this.isLoading = false;
  }

  cancelEdit(): void {
    this.resetForm();
  }
} 