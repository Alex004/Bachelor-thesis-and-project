# Generated by Django 3.1.7 on 2021-05-14 14:16

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0002_auto_20210514_1706'),
    ]

    operations = [
        migrations.CreateModel(
            name='Element',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
            ],
        ),
        migrations.RemoveField(
            model_name='route',
            name='location',
        ),
        migrations.CreateModel(
            name='Region',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('location', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='server.location')),
            ],
        ),
        migrations.CreateModel(
            name='ElementRegion',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('element', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='server.element')),
                ('region', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='server.region')),
            ],
        ),
    ]